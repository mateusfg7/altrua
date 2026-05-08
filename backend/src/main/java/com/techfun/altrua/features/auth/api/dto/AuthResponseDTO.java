package com.techfun.altrua.features.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Objeto de resposta contendo os tokens de autenticação.
 * <p>
 * Emitido após um login bem-sucedido ou operação de refresh, fornecendo
 * as credenciais necessárias para acessar endpoints protegidos.
 * </p>
 *
 * @param accessToken  Token de curta duração (JWT) para autorização de
 *                     requisições.
 * @param refreshToken Token de longa duração utilizado para obter novos access
 *                     tokens.
 */
@Schema(description = "Resposta de autenticação contendo tokens de acesso e renovação")
public record AuthResponseDTO(
        @Schema(description = "Token de acesso JWT (Bearer) para uso nas requisições autenticadas", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsIm5hbWUiOiJHYWJyaWVsIn0...", accessMode = Schema.AccessMode.READ_ONLY) String accessToken,

        @Schema(description = "Token de renovação utilizado para estender a sessão sem necessidade de novas credenciais", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI3YjhlMWEyYyIsInR5cGUiOiJyZWZyZXNoIn0...", accessMode = Schema.AccessMode.READ_ONLY) String refreshToken) {
}
