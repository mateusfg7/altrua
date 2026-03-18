package com.techfun.altrua.security.jwt;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

/**
 * Provedor de chaves criptográficas para operações JWT.
 * 
 * <p>
 * Gerencia o segredo (secret) utilizado para assinar e verificar tokens,
 * garantindo que ele esteja em um formato Base64 válido.
 * </p>
 */
@Component
public class JwtKeyProvider {

    /** Chave secreta em formato Base64, injetada via propriedades. */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Valida se o segredo configurado é uma string Base64 válida.
     * Executado automaticamente após a injeção de dependências.
     *
     * @throws IllegalStateException se o segredo não for um Base64 válido
     */
    @PostConstruct
    private void validateSecret() {
        try {
            Decoders.BASE64.decode(jwtSecret);
        } catch (DecodingException e) {
            throw new IllegalStateException(
                "JWT secret não é Base64 válido. Verifique a variável JWT_SECRET."
            );
        }
    }

    /**
     * Obtém a chave de assinatura decodificada.
     *
     * @return objeto {@link SecretKey} pronto para uso em assinaturas HMAC-SHA
     */
    public SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
