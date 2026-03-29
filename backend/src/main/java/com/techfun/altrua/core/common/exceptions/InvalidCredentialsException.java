package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando as credenciais de autenticação (e-mail ou senha)
 * fornecidas pelo usuário são inválidas.
 * 
 * <p>
 * Esta exceção encapsula o erro de autenticação e retorna o status
 * {@link HttpStatus#UNAUTHORIZED} (401).
 * Por razões de segurança, ela utiliza uma mensagem genérica para evitar
 * a enumeração de usuários.
 * </p>
 */
public class InvalidCredentialsException extends BusinessException {

    /**
     * Constrói uma nova exceção com mensagem e status HTTP 401 pré-definidos.
     */
    public InvalidCredentialsException() {
        super("Falha na autenticação", HttpStatus.UNAUTHORIZED);
    }
}
