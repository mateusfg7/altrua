package com.techfun.altrua.features.event.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.user.domain.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
 * Entidade principal que representa um Evento no ecossistema Altrua.
 * 
 * <p>
 * Esta classe centraliza todas as informações de ações sociais, incluindo
 * geolocalização,
 * períodos de ocorrência, gestão de voluntários e integração com doações.
 * Utiliza a estratégia de <b>Soft Delete</b>, onde os registros não são
 * removidos fisicamente
 * do banco de dados, mas marcados com uma data de exclusão.
 * </p>
 * 
 * @see com.techfun.altrua.features.ong.domain.model.Ong
 * @see com.techfun.altrua.features.event.domain.enums.EventStatusEnum
 */
@Entity
@Table(name = "events")
@SQLDelete(sql = "UPDATE events SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    /** Identificador único universal do evento. */
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id", nullable = false)
    /** ONG responsável pela organização e gestão do evento. */
    private Ong ong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    /** Usuário administrador que realizou o cadastro inicial do evento. */
    private User createdByUser;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Setter(AccessLevel.NONE)
    /** Coleção de etiquetas (tags) associadas para categorização e busca. */
    private Set<Tag> tags = new HashSet<>();

    @Column(nullable = false)
    /** Título público do evento. */
    private String title;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    /** Identificador único textual para composição de URLs amigáveis. */
    private String slug;

    @Column(columnDefinition = "TEXT")
    /** Descrição detalhada sobre os objetivos e atividades do evento. */
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    /** Estado atual do ciclo de vida do evento (ex: PUBLISHED, CANCELED). */
    private EventStatusEnum status;

    @Column(name = "cover_url", length = 500)
    /** URL da imagem principal ou banner de divulgação. */
    private String coverUrl;

    @Column(name = "external_link", length = 500)
    /** Link opcional para páginas externas de inscrição ou detalhes. */
    private String externalLink;

    @Column(name = "donation_info", columnDefinition = "TEXT")
    /** Informações e instruções sobre como realizar doações para o evento. */
    private String donationInfo;

    @Column(name = "donation_external_link", length = 500)
    /** Link direto para plataformas externas de arrecadação financeira. */
    private String donationExternalLink;

    @Column(name = "accepts_volunteers", nullable = false)
    /** Indica se o evento permite a inscrição de novos voluntários. */
    private boolean acceptsVolunteers;

    @Column(name = "max_volunteers")
    /** Quantidade máxima de voluntários permitida (opcional). */
    private Integer maxVolunteers;

    @Column(precision = 10, scale = 8)
    /** Coordenada de latitude para representação em mapas. */
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    /** Coordenada de longitude para representação em mapas. */
    private BigDecimal longitude;

    @Column(name = "address_label")
    /** Descrição textual do local (Ex: Praça Central ou nome da rua). */
    private String addressLabel;

    @Column(name = "starts_at", nullable = false)
    /** Data e hora de início das atividades. */
    private Instant startsAt;

    @Column(name = "ends_at")
    /** Data e hora de encerramento previsto das atividades. */
    private Instant endsAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    /** Carimbo de data/hora de quando o registro foi criado no banco. */
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    /** Carimbo de data/hora da última modificação realizada no registro. */
    private Instant updatedAt;

    @Column(name = "deleted_at")
    @Setter(AccessLevel.NONE)
    /**
     * Carimbo de data/hora da exclusão lógica (Soft Delete). Se nulo, o registro
     * está ativo.
     */
    private Instant deletedAt;

    /**
     * Gancho de ciclo de vida executado antes da persistência inicial.
     * Define os valores de auditoria de tempo.
     */
    @PrePersist
    private void onPersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Gancho de ciclo de vida executado antes de qualquer atualização.
     * Atualiza o carimbo de data da última modificação.
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
