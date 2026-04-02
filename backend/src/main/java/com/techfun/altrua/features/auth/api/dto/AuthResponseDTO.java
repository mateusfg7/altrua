package com.techfun.altrua.features.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta para operações de autenticação.
 *
 * <p>
 * Contém os tokens necessários para o cliente
 * realizar requisições autenticadas e renovar a sessão.
 * </p>
 *
 * @param accessToken  token JWT de curta duração utilizado para autenticar
 *                     requisições
 * @param refreshToken token JWT de longa duração utilizado para renovar o
 *                     access token
 */
public record AuthResponseDTO(
        @Schema(description = "Token de acesso JWT para uso nas requisições autenticadas", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String accessToken,
        @Schema(description = "Token para renovação do access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...") String refreshToken) {
}
