package com.techfun.altrua.features.ong.api.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de requisição para promoção de um usuário a administrador de uma ONG.
 *
 * @param userId o identificador único do usuário a ser promovido
 */
public record PromoteAdminRequestDTO(@NotNull(message = "O ID do usuário é obrigatório.") UUID userId) {
}
