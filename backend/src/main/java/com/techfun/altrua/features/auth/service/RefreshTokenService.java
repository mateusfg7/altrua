package com.techfun.altrua.features.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.RefreshTokenException;
import com.techfun.altrua.features.auth.api.dto.RotateResult;
import com.techfun.altrua.features.auth.domain.RefreshToken;
import com.techfun.altrua.features.auth.repository.RefreshTokenRepository;
import com.techfun.altrua.features.user.domain.User;
import com.techfun.altrua.infra.security.jwt.JwtProvider;
import com.techfun.altrua.infra.security.userdetails.UserLookupService;
import com.techfun.altrua.infra.security.userdetails.UserPrincipal;

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
@Transactional(readOnly = true)
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
     * <p>
     * O token é armazenado no banco em formato hash SHA-256.
     * O valor original é retornado para ser enviado ao cliente.
     * </p>
     *
     * @param user usuário para o qual o refresh token será gerado
     * @return o valor original do refresh token gerado
     * @throws RefreshTokenException se houver conflito ao persistir o token
     */
    @Transactional
    public String create(User user) {
        refreshTokenRepository.deleteAllByUser(user);

        UserDetails userDetails = userLookupService.loadById(user.getId());
        String token = jwtProvider.generateRefreshToken(userDetails);
        Instant expiration = Instant.now().plusMillis(refreshTokenExpiration);
        RefreshToken refreshToken = new RefreshToken(hashToken(token), user, expiration);
        try {
            refreshTokenRepository.save(refreshToken);
            return token;
        } catch (DataIntegrityViolationException ex) {
            throw new RefreshTokenException("Erro ao criar o novo refresh token");
        }
    }

    /**
     * Valida um refresh token verificando sua existência e expiração.
     *
     * <p>
     * A busca é realizada pelo hash SHA-256 do token recebido.
     * </p>
     *
     * @param token o valor original do refresh token a ser validado
     * @return o {@link RefreshToken} encontrado e válido
     * @throws RefreshTokenException se o token não for encontrado ou estiver
     *                               expirado
     */
    public RefreshToken validate(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashToken(token))
                .orElseThrow(() -> new RefreshTokenException("Refresh token não encontrado"));

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            throw new RefreshTokenException("Refresh token expirado");
        }

        return refreshToken;
    }

    /**
     * Rotaciona o refresh token, deletando o atual e gerando um novo.
     *
     * <p>
     * Operação atômica — se a criação do novo token falhar, a deleção
     * do token atual também é revertida pelo {@link Transactional}.
     * O novo token é armazenado em formato hash SHA-256.
     * </p>
     *
     * @param token o valor original do refresh token a ser rotacionado
     * @return {@link RotateResult} contendo o novo token original e o usuário
     *         vinculado
     * @throws RefreshTokenException se o token for inválido, expirado
     *                               ou houver erro ao persistir o novo token
     */
    @Transactional
    public RotateResult rotate(String token) {
        RefreshToken refreshToken = validate(token);

        refreshTokenRepository.delete(refreshToken);

        String newToken = jwtProvider.generateRefreshToken(new UserPrincipal(refreshToken.getUser()));
        Instant expiration = Instant.now().plusMillis(refreshTokenExpiration);

        try {
            refreshTokenRepository.save(new RefreshToken(hashToken(newToken), refreshToken.getUser(), expiration));
            return new RotateResult(newToken, refreshToken.getUser());
        } catch (DataIntegrityViolationException e) {
            throw new RefreshTokenException("Erro ao rotacionar refresh token");
        }
    }

    /**
     * Remove o refresh token do banco, encerrando a sessão do usuário.
     *
     * <p>
     * Utilizado no logout para invalidar a sessão do usuário.
     * A busca é realizada pelo hash SHA-256 do token recebido.
     * </p>
     *
     * @param token o valor original do refresh token a ser removido
     * @throws RefreshTokenException se o token não for encontrado
     */
    @Transactional
    public void revoke(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashToken(token))
                .orElseThrow(() -> new RefreshTokenException("Refresh token não encontrado"));

        refreshTokenRepository.delete(refreshToken);
    }

    /**
     * Gera o hash SHA-256 do token para armazenamento seguro no banco.
     *
     * @param token o valor original do token a ser hasheado
     * @return representação hexadecimal do hash SHA-256
     * @throws IllegalStateException se o algoritmo SHA-256 não estiver disponível
     *                               na JVM
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Algoritmo de hash SHA-256 não encontrado");
        }
    }
}