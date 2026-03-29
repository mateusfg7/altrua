package com.techfun.altrua.core.common.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Classe base abstrata para exceções de regras de negócio da aplicação.
 * 
 * <p>
 * Esta classe estende {@link RuntimeException} e permite que exceções
 * específicas
 * de domínio carreguem um código de status HTTP correspondente. Ela é projetada
 * para
 * ser interceptada por um manipulador global de exceções
 * ({@code @ControllerAdvice}),
 * padronizando a resposta de erro da API.
 * </p>
 */
public abstract class BusinessException extends RuntimeException {

    /**
     * O status HTTP que será retornado na resposta da API.
     */
    private final HttpStatus status;

    /**
     * Constrói uma nova exceção de negócio.
     * 
     * @param message A mensagem detalhando o erro de negócio (exibida no campo
     *                'detail' do ProblemDetail).
     * @param status  O {@link HttpStatus} que melhor representa o erro (ex: 400
     *                para erro de cliente, 409 para conflito).
     */
    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Recupera o status HTTP associado a esta exceção.
     * 
     * @return O {@link HttpStatus} definido no momento da criação da exceção.
     */
    public HttpStatus getStatus() {
        return status;
    }
}
