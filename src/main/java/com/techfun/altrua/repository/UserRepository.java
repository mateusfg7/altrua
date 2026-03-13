package com.techfun.altrua.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techfun.altrua.entities.User;

/**
 * Repositório Spring Data JPA para a entidade {@link User}.
 *
 * <p>
 * Fornece métodos CRUD (Create, Read, Update, Delete) para a entidade User,
 * além de consultas personalizadas. A chave primária da entidade é do tipo {@link UUID}.
 * </p>
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Verifica se um usuário com o e-mail especificado já existe no banco de dados.
     *
     * @param email o e-mail a ser verificado
     * @return {@code true} se um usuário com o e-mail existir, {@code false} caso contrário
     */
    public boolean existsByEmail(String email);
}
