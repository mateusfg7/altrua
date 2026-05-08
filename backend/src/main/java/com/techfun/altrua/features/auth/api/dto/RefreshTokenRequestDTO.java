package com.techfun.altrua.features.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para operações que utilizam o token de renovação (refresh token).
 * <p>
 * Este objeto é utilizado para estender a sessão do usuário ou para invalidar
 * o acesso durante o encerramento da sessão (logout).
 * </p>
 *
 * @param token O refresh token recebido anteriormente no login ou na última
 *              renovação.
 */
@Schema(description = "Dados de requisição para renovação de acesso ou logout")
public record RefreshTokenRequestDTO(
        @Schema(description = "Token de renovação (Refresh Token)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI3YjhlMWEyYyIsInR5cGUiOiJyZWZyZXNoIn0...", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O token de renovação é obrigatório") String token) {
}
