package com.techfun.altrua.features.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de requisição para alteração do e-mail do usuário autenticado.
 *
 * <p>
 * A senha atual é exigida para confirmar a identidade do usuário
 * antes de aplicar a alteração.
 * </p>
 */
@Schema(description = "Dados para alteração de e-mail")
public record ChangeEmailRequestDTO(
        @Schema(description = "Senha atual da conta para confirmação", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A senha atual é obrigatória") String currentPassword,

        @Schema(description = "Novo e-mail para cadastro", example = "novo@altrua.org", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O novo e-mail é obrigatório") @Email(message = "O e-mail informado é inválido") String newEmail) {
}
