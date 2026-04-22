package com.techfun.altrua.features.ong.api.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de requisição para promoção de um usuário a administrador de uma ONG.
 *
 * @param userId o identificador único do usuário a ser promovido
 */
@Schema(description = "Dados de requisição para promover um usuário ao cargo de administrador")
public record PromoteAdminRequestDTO(

        @Schema(description = "Identificador único (UUID) do usuário que receberá as permissões de administrador", example = "a1b2c3d4-e5f6-7g8h-9i0j-k1l2m3n4o5p6", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "O ID do usuário é obrigatório.") UUID userId) {
}
