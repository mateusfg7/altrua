package com.techfun.altrua.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

/**
 * Componente responsável pela geração de tokens JWT.
 *
 * <p>
 * Cria tokens assinados digitalmente, configurando claims (reivindicações),
 * data de emissão e expiração com base nas configurações da aplicação.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class JwtProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    private final JwtKeyProvider jwtKeyProvider;

    /** Tempo de expiração do token em milissegundos. Definido no arquivo de propriedades. */
    @Value("${jwt.expiration}")
    private int jwtExpiration;

    /**
     * Gera um token JWT para um usuário autenticado.
     *
     * <p>
     * Define o ID do usuário como "subject" e inclui as roles (autoridades)
     * no payload (claims) do token.
     * </p>
     *
     * @param userDetails os detalhes do usuário para o qual o token será gerado
     * @return a string do token JWT gerado
     */
    public String generateToken(UserDetails userDetails) {
        logger.debug("Gerando token para usuário: {}", userDetails.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER"));

        return buildToken(claims, userDetails.getUsername());
    }

    /**
     * Constrói a string do token JWT.
     *
     * @param claims  mapa de dados (claims) a serem inseridos no payload
     * @param subject o assunto do token (geralmente o username ou ID)
     * @return token JWT assinado e compactado
     */
    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(jwtKeyProvider.getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
}
