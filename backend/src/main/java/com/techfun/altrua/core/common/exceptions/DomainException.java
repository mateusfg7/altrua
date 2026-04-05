package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando ocorre uma violação de regra de negócio ou restrição
 * do domínio.
 * <p>
 * Esta exceção indica que a operação falhou devido a dados de entrada que,
 * embora tecnicamente
 * válidos (sintaxe), violam a lógica ou o estado consistente do sistema. Por
 * padrão,
 * resulta em um erro {@code 400 Bad Request}.
 * </p>
 */
public class DomainException extends BusinessException {

    /**
     * Constrói uma nova {@code DomainException} com uma mensagem detalhada.
     *
     * @param message O motivo da falha na regra de domínio.
     */
    public DomainException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

}
