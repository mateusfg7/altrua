package com.techfun.altrua.infra.security.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Componente responsável pela geração de tokens JWT.
 *
 * <p>
 * Cria tokens assinados digitalmente, configurando claims (reivindicações),
 * data de emissão e expiração com base nas configurações da aplicação.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtKeyProvider jwtKeyProvider;

    /**
     * Tempo de expiração do token em milissegundos. Definido no arquivo de
     * propriedades.
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Tempo de expiração do refresh token em milissegundos. Definido no arquivo de
     * propriedades.
     */
    @Value("${jwt.refresh-token-expiration}")
    private long jwtRefreshTokenExpiration;

    /**
     * Gera um Access Token JWT para um usuário autenticado.
     *
     * <p>
     * O token gerado possui tempo de expiração curto (definido em
     * {@code jwt.expiration})
     * e contém as seguintes informações no payload:
     * <ul>
     * <li><b>sub:</b> Identificador único do usuário (Username).</li>
     * <li><b>role:</b> A autoridade principal do usuário.</li>
     * <li><b>tokenType:</b> Identificador de tipo definido como "access".</li>
     * </ul>
     * </p>
     *
     * @param userDetails Os detalhes do usuário autenticado.
     * @return String contendo o Access Token assinado.
     */
    public String generateToken(UserDetails userDetails) {
        log.debug("Gerando token para usuário: {}", userDetails.getUsername());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER"));
        claims.put("tokenType", "access");

        return buildToken(claims, userDetails.getUsername(), jwtExpiration);
    }

    /**
     * Gera um Refresh Token JWT de longa duração para renovação de acesso.
     *
     * <p>
     * Este token possui tempo de expiração estendido (definido em
     * {@code jwt.refresh-token-expiration})
     * e não carrega autoridades (roles), minimizando o impacto em caso de
     * vazamento.
     * Contém o claim {@code tokenType} definido como "refresh" para segregação
     * lógica de uso.
     * </p>
     *
     * @param userDetails Os detalhes do usuário autenticado.
     * @return String contendo o Refresh Token assinado.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(Map.of("tokenType", "refresh"), userDetails.getUsername(), jwtRefreshTokenExpiration);
    }

    /**
     * Constrói a string do token JWT com os parâmetros fornecidos.
     *
     * <p>
     * Método interno reutilizado tanto para access token quanto para refresh token,
     * variando apenas os claims e o tempo de expiração.
     * </p>
     *
     * @param claims     mapa de dados adicionais a serem inseridos no payload
     * @param subject    o assunto do token — nesta implementação, o UUID do usuário
     * @param expiration tempo de expiração em milissegundos a partir do momento
     *                   atual
     * @return token JWT assinado com HS256 e compactado
     */
    private String buildToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(jwtKeyProvider.getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }
}
