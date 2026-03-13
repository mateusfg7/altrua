package com.techfun.altrua.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * Objeto de Transferência de Dados (DTO) para a requisição de registro de usuário.
 * Contém os campos necessários para criar uma nova conta e suas respectivas validações.
 */
@Getter
public class RegisterRequestDTO {
    
    /** E-mail do usuário. Deve ser válido e não pode estar em branco. */
    @NotBlank
    @Email
    private String email;

    /** Senha do usuário. Deve ter no mínimo 8 caracteres e não pode estar em branco. */
    @NotBlank
    @Size(min = 8)
    private String password;

    /** Nome completo do usuário. Não pode estar em branco. */
    @NotBlank
    private String name;
}