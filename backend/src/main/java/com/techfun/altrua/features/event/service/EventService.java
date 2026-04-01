package com.techfun.altrua.features.event.service;

import java.util.Set;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.util.SlugUtils;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.Tag;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.ong.service.OngService;
import com.techfun.altrua.features.user.domain.User;

import jakarta.transaction.Transactional;
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

    private final EventRepository eventRepository;
    private final OngRepository ongRepository;
    private final OngService ongService;
    private final TagService tagService;

    /**
     * Realiza o cadastro de um novo evento para uma organização específica.
     * 
     * <p>
     * O processo executa as seguintes operações:
     * <ul>
     * <li>Valida se o usuário logado possui privilégios de administrador na ONG via
     * {@link OngService}.</li>
     * <li>Processa o conjunto de tags, garantindo a persistência idempotente via
     * {@link TagService}.</li>
     * <li>Gera um slug único a partir do título do evento, adicionando sufixo se
     * necessário.</li>
     * <li>Persiste o evento e estabelece os vínculos relacionais com a ONG e o
     * Criador.</li>
     * </ul>
     * </p>
     *
     * @param ongId   UUID da ONG proprietária do evento.
     * @param request DTO contendo os dados de entrada validados.
     * @param creator Instância do usuário autenticado que está realizando a
     *                operação.
     * @return A entidade {@link Event} persistida e associada às suas respectivas
     *         tags.
     * @throws DuplicateResourceException                                Caso ocorra
     *                                                                   uma
     *                                                                   violação de
     *                                                                   unicidade
     *                                                                   de slug
     *                                                                   após
     *                                                                   tratamento.
     * @throws org.springframework.security.access.AccessDeniedException Se o
     *                                                                   usuário não
     *                                                                   tiver
     *                                                                   permissão
     *                                                                   administrativa.
     * @throws RuntimeException                                          Para falhas
     *                                                                   técnicas
     *                                                                   inesperadas
     *                                                                   durante a
     *                                                                   persistência.
     */
    @Transactional
    public Event register(UUID ongId, RegisterEventRequestDTO request, User creator) {
        ongService.validateAdminPermission(ongId, creator.getId());
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
                log.warn("Conflito de unicidade ao cadastrar evento. Constraint: {}", cve.getConstraintName());
                throw new DuplicateResourceException(
                        "Já existe um evento cadastrado com os dados fornecidos.");
            }

            log.error("Erro técnico inesperado ao cadastrar evento", ex);
            throw new RuntimeException("Não foi possível processar o cadastro do evento no momento.");
        }
    }
}
