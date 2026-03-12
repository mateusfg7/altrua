package com.techfun.altrua.entities;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import lombok.Getter;

/**
 * Entidade que representa um usuário no sistema.
 *
 * <p>
 * Mapeada para a tabela {@code user} no banco de dados. Os campos
 * {@code createdAt} e {@code updatedAt} são gerenciados automaticamente
 * pelo Spring Data e pelo método {@link #touch()}, respectivamente.
 * </p>
 */
@Entity
@Table(name = "user")
public class User {

  /** Identificador único do usuário, gerado automaticamente como UUID. */
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Getter
  private UUID id;

  /** Nome completo do usuário. */
  @Column(nullable = false)
  @Getter
  private String name;

  /** Endereço de e-mail do usuário. Deve ser único no sistema. */
  @Column(nullable = false, unique = true)
  @Getter
  private String email;

  /**
   * Senha do usuário armazenada em formato hash. Tamanho máximo de 500
   * caracteres.
   */
  @Column(nullable = false, length = 500)
  @Getter
  private String password;

  /** URL do avatar do usuário. Campo opcional. */
  @Column(name = "avatar_url")
  @Getter
  private String avatarUrl;

  /**
   * Data e hora de criação do registro. Definida automaticamente e não pode ser
   * alterada.
   */
  @Column(name = "created_at", nullable = false, updatable = false)
  @Getter
  private Instant createdAt;

  /**
   * Data e hora da última atualização do registro. Atualizada automaticamente a
   * cada modificação.
   */
  @Column(name = "updated_at", nullable = false)
  @Getter
  private Instant updatedAt;

  /**
   * Construtor padrão exigido pelo JPA. Não deve ser utilizado diretamente.
   * Utilize os métodos estáticos {@link #create} para instanciar a entidade.
   */
  protected User() {
  }

  /**
   * Construtor privado com campos obrigatórios.
   *
   * @param name     nome do usuário
   * @param email    e-mail do usuário
   * @param password senha do usuário (já em formato hash)
   */
  private User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;

    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  /**
   * Construtor privado com campos obrigatórios e avatar.
   *
   * @param name      nome do usuário
   * @param email     e-mail do usuário
   * @param password  senha do usuário (já em formato hash)
   * @param avatarUrl URL do avatar do usuário
   */
  private User(String name, String email, String password, String avatarUrl) {
    this(name, email, password);
    this.avatarUrl = avatarUrl;
  }

  /**
   * Construtor privado completo, incluindo datas de auditoria.
   *
   * @param name      nome do usuário
   * @param email     e-mail do usuário
   * @param password  senha do usuário (já em formato hash)
   * @param avatarUrl URL do avatar do usuário
   * @param createdAt data e hora de criação
   * @param updatedAt data e hora da última atualização
   */
  private User(String name, String email, String password, String avatarUrl, Instant createdAt, Instant updatedAt) {
    this(name, email, password, avatarUrl);
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  /**
   * Cria um novo usuário com os campos obrigatórios.
   *
   * @param name     nome do usuário
   * @param email    e-mail do usuário
   * @param password senha do usuário (já em formato hash)
   * @return nova instância de {@link User}
   */
  public static User create(String name, String email, String password) {
    return new User(name, email, password);
  }

  /**
   * Cria um novo usuário com os campos obrigatórios e um avatar.
   *
   * @param name      nome do usuário
   * @param email     e-mail do usuário
   * @param password  senha do usuário (já em formato hash)
   * @param avatarUrl URL do avatar do usuário
   * @return nova instância de {@link User}
   */
  public static User create(String name, String email, String password, String avatarUrl) {
    return new User(name, email, password, avatarUrl);
  }

  /**
   * Cria um novo usuário com todos os campos, incluindo datas de auditoria.
   * Útil para reconstituição de entidades a partir de fontes externas.
   *
   * @param name      nome do usuário
   * @param email     e-mail do usuário
   * @param password  senha do usuário (já em formato hash)
   * @param avatarUrl URL do avatar do usuário
   * @param createdAt data e hora de criação
   * @param updatedAt data e hora da última atualização
   * @return nova instância de {@link User}
   */
  public static User create(String name, String email, String password, String avatarUrl, Instant createdAt,
      Instant updatedAt) {
    return new User(name, email, password, avatarUrl, createdAt, updatedAt);
  }

  /**
   * Atualiza o nome do usuário e registra o momento da alteração.
   *
   * @param name novo nome do usuário
   */
  public void setName(String name) {
    this.name = name;
    this.touch();
  }

  /**
   * Atualiza o e-mail do usuário e registra o momento da alteração.
   *
   * @param email novo e-mail do usuário
   */
  public void setEmail(String email) {
    this.email = email;
    this.touch();
  }

  /**
   * Atualiza a senha do usuário e registra o momento da alteração.
   *
   * @param password nova senha em formato hash
   */
  public void setPassword(String password) {
    this.password = password;
    this.touch();
  }

  /**
   * Atualiza a URL do avatar do usuário e registra o momento da alteração.
   *
   * @param avatarUrl nova URL do avatar
   */
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    this.touch();
  }

  /**
   * Atualiza o campo {@code updatedAt} com o instante atual.
   * Chamado internamente sempre que um campo da entidade é modificado.
   */
  private void touch() {
    this.updatedAt = Instant.now();
  }
}