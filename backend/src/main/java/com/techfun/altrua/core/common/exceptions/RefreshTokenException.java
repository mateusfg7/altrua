package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada durante falhas no processo de renovação de tokens (Refresh
 * Token).
 * 
 * <p>
 * Esta exceção indica que o token de renovação é inválido, expirou ou não
 * foi encontrado no banco de dados. Retorna o status
 * {@link HttpStatus#UNAUTHORIZED} (401), sinalizando
 * ao cliente que a sessão não pode ser renovada e um novo login é necessário.
 * </p>
 */
public class RefreshTokenException extends BusinessException {

    /**
     * Constrói uma nova exceção com uma mensagem detalhando o motivo da falha.
     * 
     * @param message Detalhamento do erro (ex: "Refresh token expirado", "Token não
     *                encontrado").
     */
    public RefreshTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}