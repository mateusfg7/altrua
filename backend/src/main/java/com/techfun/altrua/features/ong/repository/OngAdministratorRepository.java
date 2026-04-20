package com.techfun.altrua.features.ong.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Verifica se um usuário é o criador de uma ONG.
     *
     * @param userId o identificador único do usuário
     * @param ongId  o identificador único da ONG
     * @return um {@link Optional} contendo {@code true} se o usuário for o criador,
     *         {@code false} se for administrador comum, ou vazio se não houver
     *         vínculo
     */
    @Query("SELECT oa.creator FROM OngAdministrator oa WHERE oa.user.id = :userId AND oa.ong.id = :ongId")
    public Optional<Boolean> checkIsCreator(@Param("userId") UUID userId, @Param("ongId") UUID ongId);
}
