package com.techfun.altrua.features.ong.domain.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Chave primária composta para a entidade {@link OngAdministrator}.
 * 
 * <p>
 * Esta classe define a unicidade do vínculo entre um Usuário e uma ONG,
 * permitindo que o JPA gerencie a tabela de junção com atributos adicionais.
 * </p>
 */
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OngAdministratorId implements Serializable {

    /** Identificador do usuário vinculado. */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /** Identificador da ONG vinculada. */
    @Column(name = "ong_id", nullable = false)
    private UUID ongId;
}
