package com.techfun.altrua.features.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * DTO de requisição para operações que envolvem refresh token.
 *
 * <p>
 * Utilizado nos endpoints {@code POST /auth/refresh} e
 * {@code POST /auth/logout},
 * recebendo o refresh token enviado pelo cliente.
 * </p>
 */
@Getter
public class RefreshTokenRequestDTO {

    /**
     * Refresh token enviado pelo cliente. Não pode estar em branco.
     */
    @NotBlank
    private String token;
}
