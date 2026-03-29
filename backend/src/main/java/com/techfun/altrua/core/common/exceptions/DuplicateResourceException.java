package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando há uma tentativa de criar um recurso que já existe
 * no sistema (violação de unicidade).
 * 
 * <p>
 * Retorna o status {@link HttpStatus#CONFLICT} (409).
 * </p>
 */
public class DuplicateResourceException extends BusinessException {

    /**
     * Constrói a exceção com uma mensagem específica sobre o recurso duplicado.
     * 
     * @param message Detalhamento do conflito (ex: "E-mail já cadastrado").
     */
    public DuplicateResourceException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}