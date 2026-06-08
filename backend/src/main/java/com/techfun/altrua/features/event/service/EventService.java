package com.techfun.altrua.features.event.service;

import java.time.Instant;
import java.util.Collections;
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
import com.techfun.altrua.features.event.api.EventMapper;
import com.techfun.altrua.features.event.api.EventSpecification;
import com.techfun.altrua.features.event.api.dto.EventFilterDTO;
import com.techfun.altrua.features.event.api.dto.EventListResponseDTO;
import com.techfun.altrua.features.event.api.dto.EventResponseDTO;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.api.dto.UpdateEventRequestDTO;
import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.event.repository.EventVolunteerRepository;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.tag.domain.Tag;
import com.techfun.altrua.features.tag.service.TagService;
import com.techfun.altrua.features.user.domain.model.User;
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
    private final EventMapper eventMapper;
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
     * @return O DTO {@link EventResponseDTO} contendo os dados do evento
     *         persistido.
     * @throws DuplicateResourceException Se houver colisão de slug que não pôde ser
     *                                    resolvida.
     */
    @Transactional
    public EventResponseDTO register(UUID ongId, RegisterEventRequestDTO request) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        User creator = userRepository.getReferenceById(currentUserId);

        Ong ong = ongRepository.getReferenceById(ongId);
        Set<Tag> managedTags = tagService.getOrCreateTags(request.tags());

        String slug = SlugUtils.normalize(request.title());
        if (eventRepository.existsBySlug(slug)) {
            slug = SlugUtils.withSuffix(slug);
        }

        validateEventDates(request.startsAt(), request.endsAt());

        try {
            Event event = eventMapper.toEntity(request, slug, ong, creator);
            event.getTags().addAll(managedTags);
            Event savedEvent = eventRepository.saveAndFlush(event);
            return eventMapper.toDto(savedEvent);
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
     * Atualiza os dados de um evento existente.
     * <p>
     * O método mescla os novos dados fornecidos no DTO com a entidade persistida,
     * valida se o novo intervalo de datas é coerente com o momento atual e reflete
     * as modificações na base de dados.
     * </p>
     *
     * @param eventId Identificador único do evento a ser atualizado.
     * @param request DTO contendo as novas informações do evento.
     * @return O DTO {@link EventResponseDTO} atualizado e mapeado.
     * @throws ResourceNotFoundException Se o ID do evento fornecido não
     *                                   corresponder a nenhum registro.
     * @throws DomainException           Se as novas datas fornecidas violarem as
     *                                   regras cronológicas de negócio.
     */
    @Transactional
    public EventResponseDTO update(UUID eventId, UpdateEventRequestDTO request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento"));

        eventMapper.updateEntityFromDto(request, event);

        validateEventDates(request.startsAt(), request.endsAt());

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toDto(updatedEvent);
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
     * Recupera um evento específico pelo seu identificador, enriquecido com
     * métricas de voluntariado.
     * <p>
     * Nota: Embora recupere um registro único, o método atualmente mapeia o
     * resultado para um {@link EventListResponseDTO}. Caso o usuário não esteja
     * autenticado, o status de inscrição será retornado como {@code false}.
     * </p>
     *
     * @param eventId Identificador único do evento a ser recuperado.
     * @return O DTO {@link EventListResponseDTO} contendo os dados do evento,
     *         contador de participantes e o status de inscrição do usuário logado.
     * @throws ResourceNotFoundException Se nenhum evento for encontrado com o ID
     *                                   fornecido.
     */
    public EventListResponseDTO getById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento"));

        List<UUID> eventIds = List.of(eventId);

        int volunteerCount = getVolunteerCounts(eventIds).getOrDefault(eventId, 0);
        boolean isEnrolled = getEnrolledEventIdsForCurrentUser(eventIds).contains(eventId);

        return eventMapper.toListDto(event, volunteerCount, isEnrolled);
    }

    /**
     * Recupera uma página de eventos filtrados e enriquecidos com a contagem de
     * voluntários confirmados e o status de inscrição do usuário autenticado.
     * <p>
     * Caso o usuário não esteja autenticado, o campo {@code enrolled} será
     * retornado como {@code false} para todos os eventos.
     * </p>
     *
     * @param filter   Objeto contendo os critérios de filtragem (ex: tags,
     *                 localização, status).
     * @param pageable Configurações de paginação e ordenação dos resultados.
     * @return Uma {@link Page} de {@link EventListResponseDTO} contendo os dados
     *         para exibição em lista, a contagem de participantes confirmados
     *         e se o usuário autenticado está inscrito em cada evento.
     */
    public Page<EventListResponseDTO> listEvents(EventFilterDTO filter, Pageable pageable) {
        Page<Event> eventPage = eventRepository.findAll(EventSpecification.withFilter(filter), pageable);

        List<UUID> eventIds = eventPage.getContent().stream().map(Event::getId).toList();

        Map<UUID, Integer> counts = getVolunteerCounts(eventIds);
        Set<UUID> enrolledEventIds = getEnrolledEventIdsForCurrentUser(eventIds);

        return eventPage.map(event -> eventMapper.toListDto(event, counts.getOrDefault(event.getId(), 0),
                enrolledEventIds.contains(event.getId())));
    }

    /**
     * Valida a consistência cronológica das datas informadas para o evento.
     * <p>
     * Garante que o evento não seja agendado para iniciar no passado e que a
     * data de início preceda estritamente a data de término, caso ambas estejam
     * preenchidas.
     * </p>
     *
     * @param startsAt Instante de início do evento.
     * @param endsAt   Instante de término do evento (pode ser nulo).
     * @throws DomainException Se o início for retroativo ou posterior/igual ao
     *                         término.
     */
    private void validateEventDates(Instant startsAt, Instant endsAt) {
        Instant now = Instant.now();

        if (startsAt != null && startsAt.isBefore(now)) {
            throw new DomainException("A data de início do evento não pode ser no passado.");
        }

        if (startsAt != null && endsAt != null && !startsAt.isBefore(endsAt)) {
            throw new DomainException("A data de início deve ser anterior à data de término.");
        }
    }

    /**
     * Recupera o total de voluntários confirmados para uma lista específica de IDs
     * de eventos.
     * <p>
     * Realiza uma consulta agregada no banco de dados para evitar o problema de
     * consultas N+1, agrupando os resultados por evento. Eventos que não possuem
     * voluntários confirmados não serão incluídos nas chaves do mapa retornado.
     * </p>
     *
     * @param eventIds Lista contendo os UUIDs dos eventos que serão consultados.
     * @return Um {@link Map} onde a chave é o UUID do evento e o valor é a
     *         quantidade de voluntários com o status
     *         {@link VolunteerStatusEnum#CONFIRMED}.
     */
    private Map<UUID, Integer> getVolunteerCounts(List<UUID> eventIds) {
        return eventVolunteerRepository
                .countVolunteersByEventIdsAndStatus(eventIds, VolunteerStatusEnum.CONFIRMED).stream()
                .collect(Collectors.toMap(row -> (UUID) row[0], row -> ((Long) row[1]).intValue()));
    }

    /**
     * Identifica em quais eventos da lista fornecida o usuário autenticado está
     * inscrito.
     * <p>
     * O método intercepta o ID do usuário logado através do contexto de segurança.
     * Caso o usuário não esteja autenticado, a consulta ao banco de dados é omitida
     * e um conjunto vazio é retornado imediatamente.
     * </p>
     *
     * @param eventIds Lista contendo os UUIDs dos eventos para checagem de vínculo.
     * @return Um {@link Set} contendo apenas os UUIDs dos eventos nos quais o
     *         usuário atual possui uma inscrição ativa. Retorna um conjunto vazio
     *         se o usuário for anônimo.
     */
    private Set<UUID> getEnrolledEventIdsForCurrentUser(List<UUID> eventIds) {
        return SecurityUtils.getCurrentUserIdOptional()
                .map(userId -> eventVolunteerRepository
                        .findEventIdsByUserIdAndEventIds(userId, eventIds)
                        .stream()
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }
}
