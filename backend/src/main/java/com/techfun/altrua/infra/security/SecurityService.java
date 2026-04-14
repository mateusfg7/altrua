package com.techfun.altrua.infra.security;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.techfun.altrua.core.common.util.SecurityUtils;
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
     * Verifica se o usuário autenticado tem permissão para gerenciar um evento.
     * <p>
     * A permissão é concedida se o usuário for administrador da ONG proprietária do
     * evento.
     * </p>
     */
    public boolean canManageEvent(UUID eventId) {
        UUID userId = SecurityUtils.getCurrentUserId();

        boolean canManage = eventRepository.existsByIdAndOng_Administrators_User_Id(eventId, userId);

        if (!canManage) {
            log.warn("Acesso negado: Usuário {} tentou gerenciar o evento {}", userId, eventId);
        }

        return canManage;
    }

}
