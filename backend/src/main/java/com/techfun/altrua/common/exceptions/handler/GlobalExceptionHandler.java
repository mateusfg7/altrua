package com.techfun.altrua.common.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.techfun.altrua.common.dto.ErrorResponseDTO;
import com.techfun.altrua.common.exceptions.EmailAlreadyInUseException;
import com.techfun.altrua.common.exceptions.InvalidCredentialsException;
import com.techfun.altrua.common.exceptions.RefreshTokenException;

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
     * @param ex      a exceção capturada
     * @param request a requisição HTTP onde a exceção ocorreu
     * @return {@link ErrorResponseDTO} com status 409 e mensagem de conflito
     */
    @ExceptionHandler(EmailAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDTO handleEmailAlreadyInUse(EmailAlreadyInUseException ex, HttpServletRequest request) {
        return ErrorResponseDTO.of(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    /**
     * Trata exceções de credenciais inválidas no processo de autenticação.
     *
     * <p>
     * Retorna mensagem genérica para não revelar se o erro foi no e-mail
     * ou na senha, prevenindo ataques de enumeração de usuários.
     * </p>
     *
     * @param ex      a exceção capturada
     * @param request a requisição HTTP onde a exceção ocorreu
     * @return {@link ErrorResponseDTO} com status 401 e mensagem genérica
     */
    @ExceptionHandler({ BadCredentialsException.class, InvalidCredentialsException.class })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleBadCredentials(Exception ex, HttpServletRequest request) {
        return ErrorResponseDTO.of("Credenciais inválidas", HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    /**
     * Trata exceções relacionadas ao refresh token.
     *
     * <p>
     * Cobre casos de token não encontrado, revogado ou expirado,
     * retornando status 401 para que o cliente realize novo login.
     * </p>
     *
     * @param ex      a exceção capturada
     * @param request a requisição HTTP onde a exceção ocorreu
     * @return {@link ErrorResponseDTO} com status 401 e descrição do erro
     */
    @ExceptionHandler(RefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponseDTO handleRefreshTokenException(RefreshTokenException ex, HttpServletRequest request) {
        return ErrorResponseDTO.of(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }
}
