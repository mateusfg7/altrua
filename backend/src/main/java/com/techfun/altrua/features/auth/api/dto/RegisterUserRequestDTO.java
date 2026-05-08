package com.techfun.altrua.features.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de registro de novos usuários no sistema.
 * <p>
 * Este objeto transporta as informações básicas de perfil e as credenciais
 * necessárias para a criação de uma nova conta de usuário.
 * </p>
 *
 * @param email    E-mail do usuário, utilizado para login e comunicações.
 * @param password Senha de acesso (deve possuir no mínimo 8 caracteres).
 * @param name     Nome completo ou social do usuário.
 */
@Schema(description = "Dados de requisição para o registro de um novo usuário")
public record RegisterUserRequestDTO(
        @Schema(description = "E-mail para cadastro e login", example = "usuario@altrua.org", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O e-mail é obrigatório") @Email(message = "O formato do e-mail é inválido") String email,

        @Schema(description = "Senha de acesso à conta (mínimo de 8 caracteres)", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A senha é obrigatória") @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String password,

        @Schema(description = "Nome completo do usuário", example = "Gabriel Henrique", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O nome é obrigatório") String name) {
}