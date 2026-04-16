package com.techfun.altrua.core.common.util;

import java.util.UUID;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.techfun.altrua.infra.security.userdetails.UserPrincipal;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilitário para acesso centralizado aos dados de segurança do Spring
 * Security.
 * <p>
 * Fornece métodos estáticos para extração de identidade e validação do estado
 * de autenticação da thread atual.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {

    /**
     * Extrai o identificador do usuário autenticado do contexto de segurança.
     * <p>
     * Este método exige uma sessão válida e um principal do tipo
     * {@link UserPrincipal}.
     * </p>
     *
     * @return {@link UUID} do usuário logado.
     * @throws InsufficientAuthenticationException se não houver usuário
     *                                             autenticado;
     *                                             {@link AuthenticationServiceException}
     *                                             se o principal for inválido.
     */
    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            throw new InsufficientAuthenticationException("Sessão expirada ou usuário não autenticado");
        }

        if (authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser().getId();
        }

        throw new AuthenticationServiceException("O objeto Principal no SecurityContext não é do tipo UserPrincipal");
    }
}
