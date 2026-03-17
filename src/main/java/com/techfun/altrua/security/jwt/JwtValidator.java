package com.techfun.altrua.security.jwt;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Componente responsável pela validação de tokens JWT.
 *
 * <p>
 * Verifica a assinatura criptográfica e a validade temporal (expiração)
 * dos tokens recebidos nas requisições.
 * </p>
 */
@Service
public class JwtValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);

    /** Chave secreta utilizada para validar a assinatura dos tokens. */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Valida se o token pertence ao usuário informado e se não está expirado.
     *
     * <p>
     * Compara o "subject" do token (geralmente o ID ou e-mail) com o username do {@link UserDetails}.
     * </p>
     *
     * @param token       o token JWT a ser validado
     * @param userDetails os detalhes do usuário contra os quais o token será validado
     * @return {@code true} se o token for válido, {@code false} caso contrário
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Validando token: {}", token);
        
        final String subject = extractSubject(token);
        if (subject == null) {
            logger.warn("Token inválido: subject ausente ou nulo");
            return false;
        }
        return subject.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extrai o "subject" (assunto) do token.
     *
     * @param token o token JWT
     * @return o assunto contido no token (ex: ID do usuário)
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai uma reivindicação (claim) específica do token utilizando um resolver.
     *
     * @param <T>            tipo do retorno esperado
     * @param token          o token JWT
     * @param claimsResolver função para extrair o dado desejado dos claims
     * @return o dado extraído
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    /**
     * Verifica se o token já expirou.
     *
     * @param token o token JWT
     * @return {@code true} se a data de expiração for anterior à data atual
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token.
     *
     * @param token o token JWT
     * @return a data de expiração
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Analisa o token e retorna todas as reivindicações (claims).
     * Utiliza a chave secreta para verificar a assinatura do token.
     *
     * @param token o token JWT
     * @return objeto contendo os claims do payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Decodifica a chave secreta configurada para uso na verificação de assinatura.
     *
     * @return chave secreta HMAC-SHA
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
