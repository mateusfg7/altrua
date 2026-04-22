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

        @Schema(description = "Identificador único (UUID) do usuário que receberá as permissões de administrador", example = "a1b2c3d4-e5f6-4789-a0b1-c2d3e4f5a6b7", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "O ID do usuário é obrigatório.") UUID userId) {
}
