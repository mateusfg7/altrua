package com.techfun.altrua.ong;

import java.time.Instant;

import com.techfun.altrua.user.User;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OngAdministrator {

    /** Identificador composto (User ID + Ong ID). */
    @EmbeddedId
    private OngAdministratorId id;

    /** Usuário que possui privilégios administrativos. */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    /** ONG a qual o administrador está vinculado. */
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ongId")
    @JoinColumn(name = "ong_id")
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
    public OngAdministrator(User user, Ong ong, boolean isCreator) {
        this.user = user;
        this.ong = ong;
        this.creator = isCreator;
        this.id = new OngAdministratorId(user.getId(), ong.getId());
    }

    /**
     * Callback JPA executado antes da persistência para definir a data de
     * atribuição.
     */
    @PrePersist
    public void onPersist() {
        this.assignedAt = Instant.now();
    }

}
