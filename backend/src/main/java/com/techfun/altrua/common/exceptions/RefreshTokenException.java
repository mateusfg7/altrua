package com.techfun.altrua.common.exceptions;

/**
 * Exceção lançada quando ocorre um erro relacionado ao refresh token,
 * como token não encontrado, revogado ou expirado.
 */
public class RefreshTokenException extends RuntimeException {

    /**
     * @param message descrição do motivo da falha no refresh token
     */
    public RefreshTokenException(String message) {
        super(message);
    }
}