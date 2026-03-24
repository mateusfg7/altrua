package com.techfun.altrua.common.exceptions;

/**
 * Exceção lançada quando ocorre uma tentativa de registro com um e-mail já existente.
 *
 * <p>
 * Estende {@link RuntimeException}, indicando que é uma exceção não verificada
 * que ocorre durante a execução da lógica de negócios de cadastro.
 * </p>
 */
public class EmailAlreadyInUseException extends RuntimeException {

    /**
     * Constrói a exceção com uma mensagem de erro genérica para e-mail já cadastrado.
     */
    public EmailAlreadyInUseException() {
        super("E-mail já cadastrado");
    }
}
