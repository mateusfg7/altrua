package com.techfun.altrua.security.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.techfun.altrua.security.jwt.JwtValidator;
import com.techfun.altrua.security.userdetails.UserLookupService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Filtro de segurança executado uma vez por requisição para autenticação via JWT.
 *
 * <p>
 * Intercepta requisições HTTP, verifica a presença e validade do token JWT no cabeçalho
 * 'Authorization' e, se válido, configura a autenticação no contexto de segurança do Spring.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserLookupService userLookupService;
    private final JwtValidator jwtValidator;

    /**
     * Executa a lógica de filtragem interna da requisição.
     * 
     * <p>
     * Extrai o token, valida sua assinatura e expiração, carrega os dados do usuário
     * e autentica a requisição no {@link SecurityContextHolder}.
     * </p>
     *
     * @param request     a requisição HTTP recebida
     * @param response    a resposta HTTP a ser enviada
     * @param filterChain a cadeia de filtros para prosseguir com a requisição
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String subject;

        try {
            subject = jwtValidator.extractSubject(token);
        } catch (ExpiredJwtException ex) {
            sendError(response, "Token expirado");
            return;
        } catch (JwtException ex) {
            sendError(response, "Token inválido");
            return;
        }   

        if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UUID userId = UUID.fromString(subject);
            UserDetails userDetails = userLookupService.loadById(userId);

            if (jwtValidator.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                        );
                
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Método utilitário para enviar uma resposta de erro em formato JSON.
     * Utilizado quando falhas específicas de JWT (expiração, formato inválido) ocorrem dentro do filtro.
     *
     * @param response a resposta HTTP
     * @param message  a mensagem de erro a ser enviada no corpo da resposta
     * @throws IOException em caso de erro de entrada/saída ao escrever a resposta
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
