package com.techfun.altrua.features.ong.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techfun.altrua.features.ong.domain.model.Ong;

import jakarta.persistence.LockModeType;

/**
 * Repositório Spring Data JPA para a entidade {@link Ong}.
 * 
 * <p>
 * Gerencia as operações de persistência, busca por slugs e validações
 * de integridade como a existência de CNPJ duplicado.
 * </p>
 */
public interface OngRepository extends JpaRepository<Ong, UUID>, JpaSpecificationExecutor<Ong> {

    /**
     * Verifica se já existe uma ONG cadastrada com o slug fornecido.
     *
     * @param slug o identificador amigável para URL
     * @return {@code true} se o slug já estiver em uso, {@code false} caso
     *         contrário
     */
    public boolean existsBySlug(String slug);

    /**
     * Verifica se já existe uma ONG cadastrada com o CNPJ fornecido.
     *
     * @param cnpj o número do CNPJ (apenas dígitos)
     * @return {@code true} se o CNPJ já existir, {@code false} caso contrário
     */
    public boolean existsByCnpj(String cnpj);

    /**
     * Busca uma ONG pelo ID, carregando seus administradores em uma única query,
     * com lock pessimista de escrita para evitar race conditions em operações
     * de modificação da lista de administradores.
     *
     * @param id o identificador único da ONG
     * @return um {@link Optional} contendo a ONG com seus administradores, ou vazio
     *         se não encontrada
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT DISTINCT o FROM Ong o LEFT JOIN FETCH o.administrators WHERE o.id = :id")
    public Optional<Ong> findByIdWithAdministrators(@Param("id") UUID id);
}
