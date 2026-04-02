package com.techfun.altrua.features.auth.domain;

import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa um refresh token no sistema.
 *
 * <p>
 * Armazena os tokens de longa duração utilizados para renovar o access token
 * sem necessidade de reautenticação. Cada token está vinculado a um usuário
 * e pode ser revogado individualmente, permitindo logout e token rotation.
 * </p>
 *
 * <p>
 * Mapeada para a tabela {@code refresh_tokens} no banco de dados.
 * </p>
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    /** Identificador único do refresh token, gerado automaticamente como UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** Valor do refresh token JWT. Deve ser único no sistema. */
    @Column(nullable = false, unique = true, length = 64)
    @Setter(AccessLevel.NONE)
    private String token;

    /** Usuário vinculado ao refresh token. */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @Setter(AccessLevel.NONE)
    private User user;

    /** Data e hora de expiração do token. */
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /**
     * Data e hora de criação do registro. Definida automaticamente
     * e não pode ser alterada.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    /**
     * Construtor para criação de um novo refresh token.
     *
     * @param token     valor do token JWT gerado
     * @param user      usuário vinculado ao token
     * @param expiresAt data e hora de expiração do token
     */
    public RefreshToken(String token, User user, Instant expiresAt) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
    }

    /**
     * Define automaticamente a data de criação antes de persistir no banco.
     */
    @PrePersist
    private void onPersist() {
        this.createdAt = Instant.now();
    }
}
