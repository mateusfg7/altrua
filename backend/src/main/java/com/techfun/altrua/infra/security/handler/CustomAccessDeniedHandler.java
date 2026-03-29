package com.techfun.altrua.infra.security.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Manipulador personalizado para tratar exceções de acesso negado
 * ({@link AccessDeniedException}).
 *
 * <p>
 * É acionado pelo Spring Security quando um usuário autenticado tenta acessar
 * um recurso
 * para o qual não possui permissão. Retorna uma resposta HTTP 403 (Forbidden)
 * com uma mensagem de erro padronizada em JSON.
 * </p>
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * Trata a falha de acesso.
     *
     * @param request               a requisição que resultou na exceção
     * @param response              a resposta para modificar
     * @param accessDeniedException a exceção de acesso negado que foi lançada
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Acesso negado\"}");
    }
}
