package com.techfun.altrua.user;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidade que representa um usuário no sistema.
 *
 * <p>
 * Mapeada para a tabela {@code users} no banco de dados.
 * </p>
 *
 * <p>
 * Os campos {@code createdAt} e {@code updatedAt} são gerenciados
 * automaticamente pelos callbacks {@link #onPersist()} e {@link #onUpdate()},
 * respectivamente, e não podem ser definidos externamente.
 * </p>
 *
 * <p>
 * A instanciação deve ser feita exclusivamente pelos métodos estáticos
 * {@code create(...)}, nunca pelo construtor diretamente.
 * </p>
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    /**
     * Identificador único do usuário.
     *
     * <p>
     * Gerado automaticamente como UUID pelo provedor JPA no momento da
     * persistência. Não pode ser alterado após a criação.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /**
     * Nome completo do usuário.
     *
     * <p>
     * Campo obrigatório. Não pode ser {@code null}.
     * </p>
     */
    @Column(nullable = false)
    private String name;

    /**
     * Endereço de e-mail do usuário.
     *
     * <p>
     * Campo obrigatório e único no sistema. Utilizado como identificador
     * de autenticação.
     * </p>
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Senha do usuário armazenada em formato hash.
     *
     * <p>
     * Nunca deve conter a senha em texto puro. O hash deve ser gerado
     * antes da criação da entidade. Tamanho máximo de 500 caracteres.
     * </p>
     */
    @Column(nullable = false, length = 500)
    private String password;

    /**
     * URL do avatar do usuário.
     *
     * <p>
     * Campo opcional. Quando presente, deve apontar para uma imagem
     * acessível publicamente. Tamanho máximo de 500 caracteres.
     * </p>
     */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    /**
     * Data e hora de criação do registro.
     *
     * <p>
     * Definida automaticamente pelo callback {@link #onPersist()} no
     * momento da primeira persistência. Não pode ser alterada posteriormente.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    /**
     * Data e hora da última atualização do registro.
     *
     * <p>
     * Inicializada pelo callback {@link #onPersist()} e atualizada
     * automaticamente a cada modificação pelo callback {@link #onUpdate()}.
     * Não pode ser definida externamente.
     * </p>
     */
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    /**
     * Construtor padrão exigido pela especificação JPA.
     *
     * <p>
     * <strong>Não deve ser utilizado diretamente.</strong> Utilize os
     * métodos estáticos {@code create(...)} para instanciar a entidade.
     * </p>
     */
    protected User() {
    }

    /**
     * Constrói um usuário com os campos obrigatórios.
     *
     * @param name     nome completo do usuário
     * @param email    endereço de e-mail do usuário
     * @param password senha do usuário já em formato hash
     */
    private User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Constrói um usuário com os campos obrigatórios e um avatar.
     *
     * @param name      nome completo do usuário
     * @param email     endereço de e-mail do usuário
     * @param password  senha do usuário já em formato hash
     * @param avatarUrl URL da imagem de avatar do usuário
     */
    private User(String name, String email, String password, String avatarUrl) {
        this(name, email, password);
        this.avatarUrl = avatarUrl;
    }

    /**
     * Constrói um usuário com todos os campos, incluindo as datas de auditoria.
     *
     * <p>
     * Destinado à reconstituição de entidades a partir de fontes externas,
     * como eventos de integração ou migrações de dados.
     * </p>
     *
     * @param name      nome completo do usuário
     * @param email     endereço de e-mail do usuário
     * @param password  senha do usuário já em formato hash
     * @param avatarUrl URL da imagem de avatar do usuário
     * @param createdAt data e hora de criação original do registro
     * @param updatedAt data e hora da última atualização do registro
     */
    private User(String name, String email, String password, String avatarUrl, Instant createdAt, Instant updatedAt) {
        this(name, email, password, avatarUrl);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Cria um novo usuário com os campos obrigatórios.
     *
     * @param name     nome completo do usuário
     * @param email    endereço de e-mail do usuário
     * @param password senha do usuário já em formato hash
     * @return nova instância de {@link User}
     */
    public static User create(String name, String email, String password) {
        return new User(name, email, password);
    }

    /**
     * Cria um novo usuário com os campos obrigatórios e um avatar.
     *
     * @param name      nome completo do usuário
     * @param email     endereço de e-mail do usuário
     * @param password  senha do usuário já em formato hash
     * @param avatarUrl URL da imagem de avatar do usuário
     * @return nova instância de {@link User}
     */
    public static User create(String name, String email, String password, String avatarUrl) {
        return new User(name, email, password, avatarUrl);
    }

    /**
     * Cria um novo usuário com todos os campos, incluindo as datas de auditoria.
     *
     * <p>
     * Útil para reconstituição de entidades a partir de fontes externas,
     * como eventos de integração ou migrações de dados.
     * </p>
     *
     * @param name      nome completo do usuário
     * @param email     endereço de e-mail do usuário
     * @param password  senha do usuário já em formato hash
     * @param avatarUrl URL da imagem de avatar do usuário
     * @param createdAt data e hora de criação original do registro
     * @param updatedAt data e hora da última atualização do registro
     * @return nova instância de {@link User}
     */
    public static User create(String name, String email, String password, String avatarUrl, Instant createdAt,
            Instant updatedAt) {
        return new User(name, email, password, avatarUrl, createdAt, updatedAt);
    }

    /**
     * Callback JPA executado antes da persistência inicial da entidade.
     *
     * <p>
     * Inicializa {@code createdAt} e {@code updatedAt} com o instante atual
     * caso ainda não tenham sido definidos.
     * </p>
     */
    @PrePersist
    public void onPersist() {
        if (this.createdAt == null)
            this.createdAt = Instant.now();
        if (this.updatedAt == null)
            this.updatedAt = Instant.now();
    }

    /**
     * Callback JPA executado antes de cada atualização da entidade.
     *
     * <p>
     * Atualiza {@code updatedAt} com o instante atual automaticamente.
     * </p>
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }
}