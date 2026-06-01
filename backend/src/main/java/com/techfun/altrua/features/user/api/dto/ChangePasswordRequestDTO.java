package com.techfun.altrua.features.user.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de requisição para alteração da senha do usuário autenticado.
 *
 * <p>
 * A senha atual é exigida para confirmar a identidade do usuário
 * antes de aplicar a alteração.
 * </p>
 */
@Schema(description = "Dados para alteração de senha")
public record ChangePasswordRequestDTO(
        @Schema(description = "Senha atual da conta", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A senha atual é obrigatória") String currentPassword,

        @Schema(description = "Nova senha da conta (mínimo de 8 caracteres)", example = "novaSenha123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A nova senha é obrigatória") @Size(min = 8, message = "A nova senha deve ter no mínimo 8 caracteres") String newPassword) {
}
