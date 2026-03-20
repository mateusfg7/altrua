package com.techfun.altrua.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.techfun.altrua.entities.RefreshToken;
import com.techfun.altrua.entities.User;
import com.techfun.altrua.exceptions.RefreshTokenException;
import com.techfun.altrua.repository.RefreshTokenRepository;
import com.techfun.altrua.security.jwt.JwtProvider;
import com.techfun.altrua.security.userdetails.UserLookupService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelo ciclo de vida dos refresh tokens.
 *
 * <p>
 * Gerencia a criação, validação, rotação e revogação dos refresh tokens,
 * garantindo a segurança do fluxo de autenticação prolongada.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserLookupService userLookupService;
    private final JwtProvider jwtProvider;

    /** Tempo de expiração do refresh token em milissegundos. */
    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    /**
     * Cria e persiste um novo refresh token vinculado ao usuário informado.
     *
     * @param user usuário para o qual o refresh token será gerado
     * @return o refresh token persistido
     * @throws RefreshTokenException se houver conflito ao salvar o token
     */
    @Transactional
    public RefreshToken create(User user) {
        UserDetails userDetails = userLookupService.loadById(user.getId());
        String token = jwtProvider.generateRefreshToken(userDetails);
        Instant expiration = Instant.now().plusMillis(refreshTokenExpiration);
        RefreshToken refreshToken = new RefreshToken(token, user, expiration);
        try {
            return refreshTokenRepository.save(refreshToken);
        } catch (DataIntegrityViolationException ex) {
            throw new RefreshTokenException("Erro ao criar o novo refresh token");
        }
    }

    /**
     * Valida um refresh token verificando sua existência, revogação e expiração.
     *
     * @param token o valor do refresh token a ser validado
     * @return o {@link RefreshToken} encontrado e válido
     * @throws RefreshTokenException se o token não for encontrado, estiver revogado
     *                               ou expirado
     */
    public RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token não encontrado"));

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenException("Refresh token revogado");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenException("Refresh token expirado");
        }

        return refreshToken;
    }

    /**
     * Rotaciona o refresh token, revogando o atual e gerando um novo.
     *
     * <p>
     * Operação atômica — se a criação do novo token falhar, a revogação
     * do token atual também é revertida.
     * </p>
     *
     * @param token o valor do refresh token a ser rotacionado
     * @return o novo {@link RefreshToken} gerado e persistido
     * @throws RefreshTokenException se o token for inválido ou houver erro ao
     *                               salvar o novo
     */
    @Transactional
    public RefreshToken rotate(String token) {
        RefreshToken refreshToken = validate(token);

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        UserDetails userDetails = userLookupService.loadById(refreshToken.getUser().getId());
        String newToken = jwtProvider.generateRefreshToken(userDetails);
        Instant expiration = Instant.now().plusMillis(refreshTokenExpiration);

        try {
            RefreshToken newRefreshToken = new RefreshToken(newToken, refreshToken.getUser(), expiration);
            return refreshTokenRepository.save(newRefreshToken);
        } catch (DataIntegrityViolationException e) {
            throw new RefreshTokenException("Erro ao rotacionar refresh token");
        }
    }

    /**
     * Revoga um refresh token, impedindo seu uso futuro.
     *
     * <p>
     * Utilizado no logout para invalidar a sessão do usuário.
     * </p>
     *
     * @param token o valor do refresh token a ser revogado
     * @throws RefreshTokenException se o token não for encontrado
     */
    @Transactional
    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token não encontrado"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}