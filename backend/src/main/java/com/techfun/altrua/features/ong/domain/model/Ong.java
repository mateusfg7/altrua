package com.techfun.altrua.features.ong.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.techfun.altrua.features.ong.domain.enums.OngStatusEnum;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa uma Organização Não Governamental (ONG) no sistema.
 * 
 * <p>
 * Esta classe armazena todas as informações institucionais, de contato e
 * localização
 * da organização, além de gerenciar o ciclo de vida (incluindo exclusão lógica)
 * e o vínculo com seus administradores.
 * </p>
 */
@Entity
@Table(name = "ongs")
@SQLDelete(sql = "UPDATE ongs SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Ong {

    /** Identificador único da ONG. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** Lista de administradores responsáveis pela gestão da ONG no sistema. */
    @Builder.Default
    @OneToMany(mappedBy = "ong", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<OngAdministrator> administrators = new ArrayList<>();

    /** Nome oficial ou fantasia da organização. */
    @Column(nullable = false)
    private String name;

    /**
     * Identificador amigável para compor URLs. Único entre registros ativos (índice
     * parcial WHERE deleted_at IS NULL).
     */
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private String slug;

    /**
     * CNPJ da organização (apenas números, 14 dígitos). Único entre registros
     * ativos (índice parcial WHERE deleted_at IS NULL).
     */
    @Column(length = 14)
    private String cnpj;

    /** Texto descritivo sobre a missão e atividades da ONG. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** E-mail institucional para contato. */
    @Column(nullable = false)
    private String email;

    /** Telefone ou celular de contato. */
    private String phone;

    /** Categoria principal de atuação (ex: Saúde, Educação, Animal). */
    @Column(nullable = false)
    private String category;

    /** Estado operacional atual da ONG. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OngStatusEnum status;

    /** URL apontando para a imagem do logotipo. */
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /** URL apontando para a imagem de capa ou banner. */
    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    /** Informações e orientações sobre como realizar doações para a ONG. */
    @Column(name = "donation_info", columnDefinition = "TEXT")
    private String donationInfo;

    /** Coordenada de latitude para geolocalização. */
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    /** Coordenada de longitude para geolocalização. */
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    /** Instante em que o registro foi criado. */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    /** Instante da última atualização dos dados. */
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    /**
     * Instante em que a exclusão lógica foi realizada. Se nulo, o registro está
     * ativo.
     */
    @Column(name = "deleted_at")
    @Setter(AccessLevel.NONE)
    private Instant deletedAt;

    /*
     * Quantidade de eventos ativos que a ONG possui.
     */
    @Formula("(SELECT COUNT(e.id) FROM events e WHERE e.ong_id = {alias}.id AND e.deleted_at IS NULL)")
    @Setter(AccessLevel.NONE)
    private Long activeEventCount;

    /**
     * Adiciona um novo administrador à organização, estabelecendo o vínculo
     * bidirecional.
     *
     * @param administrator o vínculo administrativo a ser adicionado
     */
    public void addAdministrator(OngAdministrator administrator) {
        this.administrators.add(administrator);
        administrator.setOng(this);
    }

    /**
     * Remove um administrador da organização.
     * 
     * <p>
     * Regra de negócio: O administrador marcado como criador não pode ser removido
     * para garantir que a ONG sempre tenha um responsável principal.
     * </p>
     *
     * @param administrator o vínculo administrativo a ser removido
     * @throws IllegalStateException se houver tentativa de remover o criador
     */
    public void removeAdministrator(OngAdministrator administrator) {
        if (administrator.isCreator()) {
            throw new IllegalStateException("O criador da ONG não pode ser removido.");
        }
        this.administrators.remove(administrator);
        administrator.setOng(null);
    }

    /**
     * Callback JPA executado antes da persistência inicial.
     */
    @PrePersist
    private void onPersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Callback JPA executado antes de cada atualização.
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

}