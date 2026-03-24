package com.techfun.altrua.common.exceptions;

/**
 * Exceção lançada quando as credenciais fornecidas (e-mail ou senha) são
 * inválidas.
 *
 * <p>
 * Utilizada durante o processo de autenticação para indicar falha sem revelar
 * qual campo específico está incorreto, preservando a segurança da aplicação.
 * </p>
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constrói a exceção com a mensagem padrão "Credenciais inválidas".
     */
    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }
}
