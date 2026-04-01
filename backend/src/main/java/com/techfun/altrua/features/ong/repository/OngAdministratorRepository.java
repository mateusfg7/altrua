package com.techfun.altrua.features.ong.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techfun.altrua.features.ong.domain.model.OngAdministrator;

/**
 * Repositório para gerenciamento de persistência da entidade
 * {@link OngAdministrator}.
 * <p>
 * Esta interface provê métodos para consulta de vínculos administrativos entre
 * usuários
 * e Organizações Não Governamentais (ONGs), servindo como base para o sistema
 * de
 * controle de acesso (RBAC) do domínio.
 * </p>
 * 
 * @see OngAdministrator
 */
public interface OngAdministratorRepository extends JpaRepository<OngAdministrator, UUID> {

    /**
     * Verifica se existe um registro de administrador para a combinação específica
     * de ONG e usuário.
     *
     * @param ongId  Identificador único da ONG.
     * @param userId Identificador único do usuário.
     * @return {@code true} se o vínculo de administração existir; {@code false}
     *         caso contrário.
     */
    public boolean existsByOngIdAndUserId(UUID ongId, UUID userId);

}
