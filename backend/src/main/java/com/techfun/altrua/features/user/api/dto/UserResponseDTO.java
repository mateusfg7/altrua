package com.techfun.altrua.features.user.api.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de resposta com os dados do perfil do usuário.
 *
 * <p>
 * Expõe apenas os campos necessários para o cliente, omitindo
 * informações sensíveis como senha e papel de acesso.
 * </p>
 *
 * @param id        identificador único do usuário
 * @param name      nome completo do usuário
 * @param email     endereço de e-mail utilizado no cadastro e login
 * @param avatarUrl URL da imagem de avatar do usuário, podendo ser {@code null}
 *                  caso não configurada
 * @param createdAt data e hora de criação do registro em UTC
 * @param updatedAt data e hora da última atualização do registro em UTC
 */
@Schema(description = "Dados do perfil do usuário")
public record UserResponseDTO(
        @Schema(description = "Identificador único do usuário", example = "550e8400-e29b-41d4-a716-446655440000") UUID id,

        @Schema(description = "Nome completo do usuário", example = "Gabriel Henrique") String name,

        @Schema(description = "Endereço de e-mail utilizado no cadastro e login", example = "usuario@altrua.org") String email,

        @Schema(description = "URL da imagem de avatar do usuário", example = "https://exemplo.com/avatar.png", nullable = true) String avatarUrl,

        @Schema(description = "Data e hora de criação do registro em UTC", example = "2024-01-15T10:30:00Z") Instant createdAt,

        @Schema(description = "Data e hora da última atualização do registro em UTC", example = "2024-01-15T10:30:00Z") Instant updatedAt) {
}