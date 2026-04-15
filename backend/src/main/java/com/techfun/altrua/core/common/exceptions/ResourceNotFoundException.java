package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado no sistema.
 *
 * <p>
 * Retorna o status {@link HttpStatus#NOT_FOUND} (404).
 * </p>
 */
public class ResourceNotFoundException extends BusinessException {
    /**
     * Constrói a exceção com o nome do recurso que não foi encontrado.
     *
     * @param resourceName Nome do recurso não encontrado (ex: "Usuário", "Evento").
     */
    public ResourceNotFoundException(String resourceName) {
        super(resourceName + " não encontrado", HttpStatus.NOT_FOUND);
    }
}
