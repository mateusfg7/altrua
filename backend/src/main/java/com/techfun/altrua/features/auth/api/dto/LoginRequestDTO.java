package com.techfun.altrua.features.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de autenticação de usuários.
 * <p>
 * Transporta as credenciais básicas necessárias para a geração do token de
 * acesso e início da sessão no sistema.
 * </p>
 *
 * @param email    E-mail cadastrado do usuário.
 * @param password Senha de acesso do usuário.
 */
@Schema(description = "Dados de requisição para autenticação (Login)")
public record LoginRequestDTO(
        @Schema(description = "E-mail do usuário cadastrado na plataforma", example = "usuario@altrua.org", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O formato do e-mail é inválido") String email,

        @Schema(description = "Senha secreta do usuário", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A senha é obrigatória") String password) {
}
