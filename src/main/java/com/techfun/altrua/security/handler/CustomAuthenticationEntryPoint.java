package com.techfun.altrua.security.handler;

import java.io.IOException;

import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Ponto de entrada de autenticação personalizado.
 *
 * <p>
 * É acionado pelo Spring Security quando um usuário não autenticado tenta acessar
 * um recurso protegido. Inicia o processo de autenticação, que neste caso
 * significa retornar uma resposta HTTP 401 (Unauthorized) com uma mensagem de erro
 * padronizada em JSON.
 * </p>
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Inicia o fluxo de autenticação ou, neste caso, rejeita a requisição não autenticada.
     *
     * @param request       a requisição que precisa de autenticação
     * @param response      a resposta para modificar
     * @param authException a exceção que causou a invocação deste ponto de entrada
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Não autenticado\"}");
    }
}
