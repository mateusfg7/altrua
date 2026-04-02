package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção disparada quando uma regra de negócio impede a execução de uma ação.
 * <p>
 * Diferencia-se de falhas de autenticação (401) ou de autorização de perímetro
 * (Spring Security),
 * pois é lançada pela camada de serviço quando o usuário, apesar de
 * identificado, não possui
 * os requisitos de domínio necessários para a operação (ex: tentar gerenciar
 * uma ONG da qual
 * não é administrador).
 * </p>
 * *
 * <p>
 * Esta exceção herda de {@link BusinessException} e define o status HTTP como
 * 403 (Forbidden).
 * </p>
 */
public class ForbiddenActionException extends BusinessException {

    /**
     * Constrói a exceção com uma mensagem detalhada que será exibida ao usuário
     * final.
     *
     * @param message Mensagem explicativa da violação da regra de negócio.
     */
    public ForbiddenActionException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
