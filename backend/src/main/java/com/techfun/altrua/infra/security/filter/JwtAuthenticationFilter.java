package com.techfun.altrua.infra.security.filter;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.techfun.altrua.infra.security.jwt.JwtValidator;
import com.techfun.altrua.infra.security.userdetails.UserLookupService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro de segurança executado uma vez por requisição para autenticação via
 * JWT.
 *
 * <p>
 * Intercepta requisições HTTP, verifica a presença e validade do token JWT no
 * cabeçalho
 * 'Authorization' e, se válido, configura a autenticação no contexto de
 * segurança do Spring.
 * </p>
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserLookupService userLookupService;
    private final JwtValidator jwtValidator;
    private final HandlerExceptionResolver resolver;

    public JwtAuthenticationFilter(
            UserLookupService userLookupService,
            JwtValidator jwtValidator,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.userLookupService = userLookupService;
        this.jwtValidator = jwtValidator;
        this.resolver = resolver;
    }

    /**
     * Filtro de segurança para autenticação via JWT.
     * <p>
     * Valida o token 'Bearer' no cabeçalho Authorization, verifica a integridade
     * e o tipo do acesso (Access Token) e estabelece o contexto de segurança.
     * Falhas são delegadas ao {@code HandlerExceptionResolver}.
     * </p>
     *
     * @param request     Requisição HTTP.
     * @param response    Resposta HTTP.
     * @param filterChain Cadeia de filtros.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            jwtValidator.validateTokenIntegrity(token);
            String subject = jwtValidator.extractSubject(token);

            if (subject != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UUID userId = UUID.fromString(subject);
                UserDetails userDetails = userLookupService.loadById(userId);

                if (jwtValidator.validateToken(token, userDetails) && jwtValidator.isAccessToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    throw new JwtException("Token inválido");
                }
            }

            filterChain.doFilter(request, response);

        } catch (JwtException | UsernameNotFoundException | IllegalArgumentException ex) {
            log.warn("JWT inválido na requisição {}: {}", request.getRequestURI(), ex.getMessage());
            resolver.resolveException(request, response, null, ex);
            return;
        }
    }
}
