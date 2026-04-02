package com.techfun.altrua.features.auth.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * Objeto de Transferência de Dados (DTO) para a requisição de registro de
 * usuário.
 * Contém os campos necessários para criar uma nova conta e suas respectivas
 * validações.
 */
@Getter
public class RegisterUserRequestDTO {

    /** E-mail do usuário. Deve ser válido e não pode estar em branco. */
    @Schema(description = "E-mail do usuário", example = "gabriel@altrua.com")
    @NotBlank
    @Email
    private String email;

    /**
     * Senha do usuário. Deve ter no mínimo 8 caracteres e não pode estar em branco.
     */
    @Schema(description = "Senha do usuário (mínimo 8 caracteres)", example = "senha@123")
    @NotBlank
    @Size(min = 8)
    private String password;

    /** Nome completo do usuário. Não pode estar em branco. */
    @Schema(description = "Nome completo do usuário", example = "Gabriel Henrique")
    @NotBlank
    private String name;
}