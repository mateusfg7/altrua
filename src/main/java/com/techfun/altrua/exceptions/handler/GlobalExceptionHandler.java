package com.techfun.altrua.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.techfun.altrua.dto.ErrorResponseDTO;
import com.techfun.altrua.exceptions.EmailAlreadyInUseException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manipulador global de exceções para a API.
 * 
 * <p>
 * Intercepta exceções lançadas pelos controllers e serviços, retornando
 * respostas HTTP padronizadas com mensagens de erro adequadas.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Trata a exceção {@link EmailAlreadyInUseException}.
     *
     * @param ex a exceção capturada
     * @param request a requisição HTTP onde a exceção ocorreu
     * @return um objeto {@link ErrorResponseDTO} contendo os detalhes do erro
     * @see HttpStatus#CONFLICT
     */
    @ExceptionHandler(EmailAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleEmailAlreadyInUse(EmailAlreadyInUseException ex, HttpServletRequest request) {
        return ErrorResponseDTO.of(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }
}
