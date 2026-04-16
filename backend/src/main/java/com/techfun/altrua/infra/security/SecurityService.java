package com.techfun.altrua.infra.security;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.ong.domain.model.OngAdministratorId;
import com.techfun.altrua.features.ong.repository.OngAdministratorRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço de suporte à autorização para validação de permissões de acesso.
 * <p>
 * Utilizado principalmente em expressões SpEL dentro de anotações
 * {@code @PreAuthorize}.
 * </p>
 */
@Slf4j
@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final OngAdministratorRepository ongAdministratorRepository;
    private final EventRepository eventRepository;

    /**
     * Verifica se o usuário autenticado é administrador de uma ONG específica.
     */
    public boolean isOngAdmin(UUID ongId) {
        UUID userId = SecurityUtils.getCurrentUserId();

        boolean isAdmin = ongAdministratorRepository.existsById(new OngAdministratorId(userId, ongId));

        if (!isAdmin) {
            log.warn("Acesso negado: Usuário {} tentou gerenciar a ONG {}", userId, ongId);
        }

        return isAdmin;
    }

    /**
     * Valida se o usuário autenticado possui permissão para gerenciar um evento
     * específico.
     * <p>
     * O método verifica a existência do evento e sua vinculação à ONG informada.
     * A permissão é concedida apenas se o usuário for um administrador da ONG
     * proprietária.
     * </p>
     *
     * @param ongId   Identificador da ONG para validação de integridade da rota.
     * @param eventId Identificador do evento a ser gerenciado.
     * @return {@code true} se o acesso for autorizado.
     * @throws ResourceNotFoundException Se o evento não existir ou não pertencer à
     *                                   ONG informada.
     */
    @Transactional(readOnly = true)
    public boolean canManageEvent(UUID ongId, UUID eventId) {
        Event event = eventRepository.findByIdWithOngAndAdmins(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento"));

        if (!event.getOng().getId().equals(ongId)) {
            throw new ResourceNotFoundException("Evento");
        }

        UUID userId = SecurityUtils.getCurrentUserId();

        boolean canManage = event.getOng().getAdministrators().stream()
                .anyMatch(admin -> admin.getUser().getId().equals(userId));

        if (!canManage) {
            log.warn("Acesso negado: Usuário {} tentou gerenciar o evento {} da ONG {}", userId, eventId, ongId);
        }

        return canManage;
    }

}
