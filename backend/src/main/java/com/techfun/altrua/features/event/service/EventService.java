package com.techfun.altrua.features.event.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.core.common.util.SlugUtils;
import com.techfun.altrua.features.event.api.EventSpecification;
import com.techfun.altrua.features.event.api.dto.EventFilterDTO;
import com.techfun.altrua.features.event.api.dto.EventListResponseDTO;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.event.repository.EventVolunteerRepository;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.tag.domain.Tag;
import com.techfun.altrua.features.tag.service.TagService;
import com.techfun.altrua.features.user.domain.User;
import com.techfun.altrua.features.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável pela gestão das regras de negócio de eventos.
 * 
 * <p>
 * Esta classe coordena a criação e manipulação de eventos, integrando
 * a validação de permissões administrativas, o processamento de etiquetas
 * (tags)
 * e a garantia de unicidade de slugs para acesso via URL.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventVolunteerRepository eventVolunteerRepository;
    private final OngRepository ongRepository;
    private final TagService tagService;

    /**
     * Registra um novo evento associado a uma ONG.
     * <p>
     * O método realiza a normalização e persistência de etiquetas (tags), gera um
     * slug
     * único para a URL e persiste a entidade. A validação de permissões
     * administrativas
     * é delegada à camada de segurança via {@code @PreAuthorize}.
     * </p>
     *
     * @param ongId   UUID da organização proprietária.
     * @param request DTO com os dados do evento.
     * @return O evento registrado e persistido.
     * @throws DuplicateResourceException Se houver colisão de slug que não pôde ser
     *                                    resolvida.
     */
    @Transactional
    public Event register(UUID ongId, RegisterEventRequestDTO request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User creator = userRepository.getReferenceById(currentUserId);

        Ong ong = ongRepository.getReferenceById(ongId);
        Set<Tag> managedTags = tagService.getOrCreateTags(request.tags());

        String slug = SlugUtils.normalize(request.title());
        if (eventRepository.existsBySlug(slug)) {
            slug = SlugUtils.withSuffix(slug);
        }

        try {
            Event event = request.toEntity(slug, ong, creator);
            event.getTags().addAll(managedTags);

            return eventRepository.saveAndFlush(event);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getCause() instanceof ConstraintViolationException cve) {
                if ("uk_active_event_slug".equals(cve.getConstraintName())) {
                    throw new DuplicateResourceException("Slug já existe.");
                }
            }

            log.error("Erro técnico inesperado ao cadastrar evento: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Encerra um evento atualizando seu status e data de término.
     * <p>
     * Valida as regras de transição de estado na entidade e sincroniza as
     * alterações no banco de dados.
     * </p>
     *
     * @param eventId Identificador do evento.
     * @throws ResourceNotFoundException se o ID for inválido;
     *                                   {@link DomainException} se o status atual
     *                                   não permitir o encerramento.
     */
    @Transactional
    public void endEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento"));

        event.finish();
        eventRepository.save(event);
    }

    /**
     * Recupera uma página de eventos filtrados e enriquecidos com a contagem de
     * voluntários.
     * <p>
     * O método realiza a busca paginada baseada nos filtros fornecidos e, para
     * otimizar a performance
     * e evitar o problema de consultas N+1, recupera as contagens de voluntários
     * confirmados em lote
     * (batch) antes de mapear os resultados para o DTO.
     * </p>
     *
     * @param filter   Objeto contendo os critérios de filtragem (ex: tags,
     *                 localização, status).
     * @param pageable Configurações de paginação e ordenação dos resultados.
     * @return Uma {@link Page} de {@link EventListResponseDTO} contendo os dados
     *         para exibição em lista
     *         e a contagem atualizada de participantes confirmados.
     */
    public Page<EventListResponseDTO> listEvents(EventFilterDTO filter, Pageable pageable) {
        Page<Event> eventPage = eventRepository.findAll(EventSpecification.withFilter(filter), pageable);

        List<UUID> eventIds = eventPage.getContent().stream().map(Event::getId).toList();

        Map<UUID, Integer> counts = eventVolunteerRepository
                .countVolunteersByEventIdsAndStatus(eventIds, VolunteerStatusEnum.CONFIRMED).stream()
                .collect(Collectors.toMap(row -> (UUID) row[0], row -> ((Long) row[1]).intValue()));

        return eventPage.map(event -> EventListResponseDTO.fromEntity(event, counts.getOrDefault(event.getId(), 0)));
    }
}
