package com.techfun.altrua.features.ong.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techfun.altrua.features.ong.domain.model.OngAdministrator;
import com.techfun.altrua.features.ong.domain.model.OngAdministratorId;

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
public interface OngAdministratorRepository extends JpaRepository<OngAdministrator, OngAdministratorId> {

}
