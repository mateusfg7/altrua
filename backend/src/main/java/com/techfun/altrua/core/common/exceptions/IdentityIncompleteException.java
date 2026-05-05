package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando a identidade de um usuário autenticado está incompleta
 * ou inconsistente.
 * <p>
 * É utilizada principalmente durante o fluxo de geração de tokens ou
 * autorização, quando o sistema identifica que o objeto de usuário não possui
 * as permissões (authorities) necessárias para uma operação segura, resultando
 * em uma resposta @link HttpStatus#FORBIDDEN}.
 * </p>
 */
public class IdentityIncompleteException extends BusinessException {

    /**
     * Cria uma nova instância com uma mensagem detalhada.
     *
     * @param message Descrição do erro de integridade da identidade.
     */
    public IdentityIncompleteException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

}