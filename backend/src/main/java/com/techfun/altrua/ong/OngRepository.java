package com.techfun.altrua.ong;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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
}
