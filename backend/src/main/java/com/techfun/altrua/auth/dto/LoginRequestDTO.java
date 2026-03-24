package com.techfun.altrua.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

/**
 * Objeto de Transferência de Dados (DTO) para a requisição de login.
 * Contém as credenciais necessárias (e-mail e senha) para autenticar um usuário
 * no sistema.
 */
@Getter
public class LoginRequestDTO {

    /**
     * E-mail do usuário. Deve ser um endereço de e-mail válido e não pode estar em
     * branco.
     */
    @NotBlank
    @Email
    private String email;

    /** Senha do usuário. Não pode estar em branco. */
    @NotBlank
    private String password;
}
