package com.techfun.altrua.features.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.techfun.altrua.features.auth.domain.RefreshToken;
import com.techfun.altrua.features.user.domain.User;

import jakarta.persistence.LockModeType;

/**
 * Repositório para operações de persistência da entidade {@link RefreshToken}.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Busca um refresh token pelo seu valor aplicando lock pessimista.
     *
     * <p>
     * Utiliza {@link LockModeType#PESSIMISTIC_WRITE} para garantir exclusividade
     * durante operações de escrita concorrentes, como rotação e revogação de
     * tokens.
     * </p>
     *
     * @param token o valor do refresh token a ser localizado
     * @return um {@link Optional} contendo o token encontrado, ou vazio se não
     *         existir
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Optional<RefreshToken> findByToken(String token);

    /**
     * Remove todos os refresh tokens vinculados ao usuário informado.
     *
     * <p>
     * Utilizado antes da criação de um novo token para garantir
     * que apenas um refresh token ativo por usuário exista no banco.
     * </p>
     *
     * @param user usuário cujos refresh tokens serão removidos
     */
    public void deleteAllByUser(User user);
}