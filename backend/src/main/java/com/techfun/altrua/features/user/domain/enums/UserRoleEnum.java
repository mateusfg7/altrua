package com.techfun.altrua.features.user.domain.enums;

import org.springframework.security.core.GrantedAuthority;

/**
 * Define os papéis de acesso (Roles) do sistema e suas representações de
 * autoridade.
 * *
 * <p>
 * Esta enumeração implementa {@link GrantedAuthority} para integração direta
 * com o Spring Security, garantindo que cada constante seja automaticamente
 * convertida para o formato de autoridade reconhecido pelo framework.
 * </p>
 */
public enum UserRoleEnum implements GrantedAuthority {

    /** Papel com acesso total aos recursos administrativos do sistema. */
    ADMIN,

    /** Papel de usuário padrão com permissões limitadas aos recursos comuns. */
    USER;

    /**
     * Retorna a representação da autoridade no formato esperado pelo Spring
     * Security.
     * <p>
     * Concatena o prefixo obrigatório "ROLE_" ao nome da constante (ex:
     * "ROLE_ADMIN").
     * Este método é utilizado internamente pelo framework durante a verificação de
     * permissões com {@code hasRole()}.
     * </p>
     * * @return String contendo a autoridade formatada.
     */
    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
