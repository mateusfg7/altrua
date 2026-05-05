package com.techfun.altrua.features.ong.domain.model;

import java.time.Instant;

import com.techfun.altrua.features.user.domain.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa o vínculo administrativo entre um {@link User} e uma
 * {@link Ong}.
 * 
 * <p>
 * Esta classe gerencia a associação de permissões administrativas, registrando
 * quando o acesso foi concedido e identificando se o usuário é o criador da
 * organização.
 * </p>
 */
@Entity
@Table(name = "ong_administrators")
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OngAdministrator {

    /** Identificador composto (User ID + Ong ID). */
    @EmbeddedId
    @EqualsAndHashCode.Include
    private OngAdministratorId id;

    /** Usuário que possui privilégios administrativos. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    /** ONG a qual o administrador está vinculado. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ongId")
    @JoinColumn(name = "ong_id")
    @Setter(AccessLevel.PROTECTED)
    private Ong ong;

    /** Instante em que o usuário foi designado como administrador. */
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    /** Indica se este administrador é o criador original da ONG. */
    @Column(name = "is_creator", nullable = false)
    private boolean creator;

    /**
     * Construtor para criar um novo vínculo administrativo.
     *
     * @param user      o usuário a ser promovido a administrador
     * @param ong       a organização alvo
     * @param isCreator define se o usuário é o dono/criador da ONG
     */
    private OngAdministrator(User user, Ong ong, boolean isCreator) {
        this.id = new OngAdministratorId(user.getId(), ong.getId());
        this.user = user;
        this.ong = ong;
        this.creator = isCreator;
    }

    /**
     * Cria e vincula um administrador criador à ONG.
     * <p>
     * O criador possui privilégios especiais e não pode ser removido da ONG.
     *
     * @param user o usuário a ser vinculado como criador
     * @param ong  a ONG à qual o usuário será vinculado
     * @return o {@link OngAdministrator} criado com flag de criador ativa
     */
    public static OngAdministrator createCreator(User user, Ong ong) {
        return create(user, ong, true);
    }

    /**
     * Cria e vincula um administrador comum à ONG.
     *
     * @param user o usuário a ser promovido a administrador
     * @param ong  a ONG à qual o usuário será vinculado
     * @return o {@link OngAdministrator} criado sem flag de criador
     */
    public static OngAdministrator createAdministrator(User user, Ong ong) {
        return create(user, ong, false);
    }

    /**
     * Método interno que instancia um {@link OngAdministrator} e o adiciona à lista
     * de administradores da ONG.
     *
     * @param user      o usuário a ser vinculado
     * @param ong       a ONG à qual o usuário será vinculado
     * @param isCreator {@code true} se o vínculo deve ser de criador, {@code false}
     *                  para administrador comum
     * @return o {@link OngAdministrator} criado e já associado à ONG
     */
    private static OngAdministrator create(User user, Ong ong, boolean isCreator) {
        OngAdministrator admin = new OngAdministrator(user, ong, isCreator);
        ong.addAdministrator(admin);
        return admin;
    }

    /**
     * Callback JPA executado antes da persistência para definir a data de
     * atribuição.
     */
    @PrePersist
    private void onPersist() {
        this.assignedAt = Instant.now();
    }

}
