package com.techfun.altrua.features.user.api.dto;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO de requisição para atualização parcial do perfil do usuário autenticado.
 *
 * <p>
 * Todos os campos são opcionais — apenas os informados serão atualizados.
 * </p>
 */
@Schema(description = "Dados para atualização parcial do perfil do usuário")
public record UpdateUserRequestDTO(
        @Schema(description = "Novo nome do usuário", example = "Gabriel Henrique") @Size(min = 2, max = 100, message = "O nome deve ter no mínimo 2 caracteres") String name,

        @Schema(description = "URL do novo avatar do usuário", example = "https://exemplo.com/avatar.png") @URL(message = "A URL do avatar é inválida") String avatarUrl) {
}
