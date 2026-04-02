package com.techfun.altrua.infra.security.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Ponto de entrada de autenticação que delega o tratamento de erros para o
 * controlador global.
 *
 * <p>
 * Este componente é acionado pelo Spring Security quando uma requisição não
 * autenticada
 * tenta acessar um recurso protegido. Em vez de manipular a resposta HTTP
 * diretamente,
 * ele encaminha a {@link AuthenticationException} para o
 * {@link HandlerExceptionResolver},
 * permitindo que o erro seja processado e padronizado pelo Exception Handler
 * global da aplicação.
 * </p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    public CustomAuthenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Intercepta a falha de autenticação e a delega ao resolvedor de exceções do
     * Spring MVC.
     *
     * @param request       A requisição que originou a falha de autenticação.
     * @param response      A resposta HTTP (não modificada diretamente aqui).
     * @param authException A exceção de autenticação lançada pelos filtros de
     *                      segurança.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) {
        // Redireciona a exceção da Filter Chain para o @ControllerAdvice
        resolver.resolveException(request, response, null, authException);
    }
}
