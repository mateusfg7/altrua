package com.techfun.altrua.features.user.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.user.domain.enums.UserRoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio que centraliza os dados de identidade e regras de acesso
 * do usuário.
 * 
 * <p>
 * Esta classe utiliza o padrão <b>Static Factory Methods</b> para garantir que
 * a criação de usuários siga estados válidos e imutáveis em relação ao papel
 * (Role) atribuído. O ciclo de vida temporal (criação e atualização) é
 * gerenciado automaticamente via callbacks JPA.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    /** Identificador único persistente. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** Nome completo do titular da conta. Campo obrigatório. */
    @Column(nullable = false)
    private String name;

    /**
     * Nível de autoridade do usuário.
     * Definido obrigatoriamente na criação e imutável para prevenir escalação de
     * privilégios via JPA.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private UserRoleEnum role;

    /**
     * E-mail único e obrigatório. Atua como o identificador primário para o
     * processo de login.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Representação em hash da credencial de acesso.
     * <b>Atenção:</b> Jamais deve persistir texto plano; o hashing deve ocorrer na
     * camada de serviço.
     */
    @Column(nullable = false, length = 500)
    private String password;

    /** Referência externa para a imagem de perfil. Campo opcional. */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /** Timestamp da criação do registro. Imutável após o primeiro insert. */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    /**
     * Timestamp da última modificação, atualizado automaticamente em cada update.
     */
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    /**
     * Construtor privado para reforçar a política de criação via métodos estáticos
     * nomeados.
     */
    private User(String name, String email, String password, UserRoleEnum role, String avatarUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatarUrl = avatarUrl;
    }

    /**
     * Instancia um usuário com perfil padrão de acesso (USER).
     */
    public static User createStandard(String name, String email, String password) {
        return new User(name, email, password, UserRoleEnum.USER, null);
    }

    /**
     * Instancia um usuário com privilégios administrativos totais (ADMIN).
     */
    public static User createAdmin(String name, String email, String password) {
        return new User(name, email, password, UserRoleEnum.ADMIN, null);
    }

    /**
     * Método fluente para configuração opcional do avatar após a criação.
     * 
     * @return A própria instância de {@code User} para encadeamento.
     */
    public User withAvatar(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    /**
     * Reconstrói a entidade com dados pré-existentes.
     * Uso exclusivo para mapeamento de persistência ou migração de dados.
     */
    public static User reconstruct(String name, String email, String password, UserRoleEnum role, String avatarUrl,
            Instant createdAt, Instant updatedAt) {
        User user = new User(name, email, password, role, avatarUrl);
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }

    /**
     * Sincroniza os campos temporais antes da persistência inicial.
     */
    @PrePersist
    private void onPersist() {
        if (this.createdAt == null)
            this.createdAt = Instant.now();
        if (this.updatedAt == null)
            this.updatedAt = Instant.now();
    }

    /**
     * Garante a atualização do timestamp sempre que o registro sofrer modificações.
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }
}