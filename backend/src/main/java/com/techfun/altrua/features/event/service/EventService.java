package com.techfun.altrua.features.event.service;

import java.util.Set;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.ForbiddenActionException;
import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.core.common.util.SlugUtils;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.Tag;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.repository.OngRepository;
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
public class EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final OngRepository ongRepository;
    private final TagService tagService;

    /**
     * Registra um novo evento associado a uma ONG e ao seu criador.
     *
     * <p>
     * O fluxo compreende a validação de permissões administrativas, a normalização
     * e
     * persistência idempotente de etiquetas (tags) e a geração de um identificador
     * amigável (slug) para a URL do evento.
     * </p>
     *
     * @param ongId   UUID da organização proprietária do evento.
     * @param request DTO com os dados de entrada validados.
     * @throws ForbiddenActionException   Se o criador não for administrador da ONG.
     * @throws DuplicateResourceException Se o slug gerado colidir com um evento
     *                                    ativo.
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

            return eventRepository.save(event);
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
    public void endEvent(UUID ongId, UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento"));

        if (!event.getOng().getId().equals(ongId)) {
            throw new ResourceNotFoundException("Evento");
        }

        event.finish();
        eventRepository.save(event);
    }
}
