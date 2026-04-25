package com.techfun.altrua.features.event.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.tag.domain.Tag;
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

    /** Identificador único universal do evento. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** ONG responsável pela organização e gestão do evento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id", nullable = false)
    @Setter(AccessLevel.NONE)
    private Ong ong;

    /** Usuário administrador que realizou o cadastro inicial do evento. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private User createdByUser;

    /** Coleção de etiquetas (tags) associadas para categorização e busca. */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "event_tags", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @Setter(AccessLevel.NONE)
    private Set<Tag> tags = new HashSet<>();

    /** Título público do evento. */
    @Column(nullable = false)
    private String title;

    /** Identificador único textual para composição de URLs amigáveis. */
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private String slug;

    /** Descrição detalhada sobre os objetivos e atividades do evento. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Estado atual do ciclo de vida do evento (ex: PUBLISHED, CANCELED). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventStatusEnum status;

    /** URL da imagem principal ou banner de divulgação. */
    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    /** Link opcional para páginas externas de inscrição ou detalhes. */
    @Column(name = "external_link", length = 500)
    private String externalLink;

    /** Informações e instruções sobre como realizar doações para o evento. */
    @Column(name = "donation_info", columnDefinition = "TEXT")
    private String donationInfo;

    /** Link direto para plataformas externas de arrecadação financeira. */
    @Column(name = "donation_external_link", length = 500)
    private String donationExternalLink;

    /** Indica se o evento permite a inscrição de novos voluntários. */
    @Column(name = "accepts_volunteers", nullable = false)
    private boolean acceptsVolunteers;

    /** Quantidade máxima de voluntários permitida (opcional). */
    @Column(name = "max_volunteers")
    private Integer maxVolunteers;

    /** Coordenada de latitude para representação em mapas. */
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    /** Coordenada de longitude para representação em mapas. */
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    /** Descrição textual do local (Ex: Praça Central ou nome da rua). */
    @Column(name = "address_label")
    private String addressLabel;

    /** Data e hora de início das atividades. */
    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    /** Data e hora de encerramento previsto das atividades. */
    @Column(name = "ends_at")
    private Instant endsAt;

    /** Carimbo de data/hora de quando o registro foi criado no banco. */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private Instant createdAt;

    /** Carimbo de data/hora da última modificação realizada no registro. */
    @Column(name = "updated_at", nullable = false)
    @Setter(AccessLevel.NONE)
    private Instant updatedAt;

    /**
     * Carimbo de data/hora da exclusão lógica (Soft Delete). Se nulo, o registro
     * está ativo.
     */
    @Column(name = "deleted_at")
    @Setter(AccessLevel.NONE)
    private Instant deletedAt;

    /**
     * Altera o estado do evento para finalizado e define o instante de término.
     * <p>
     * Garante a idempotência da operação e impede o encerramento de eventos
     * previamente cancelados.
     * </p>
     * * @throws DomainException se o evento estiver com status
     * {@link EventStatusEnum#CANCELED}.
     */
    public void finish() {
        if (this.status == EventStatusEnum.CANCELED) {
            throw new DomainException("Não é possível encerrar um evento que foi cancelado.");
        }

        if (this.status == EventStatusEnum.FINISHED) {
            return;
        }

        if (this.endsAt == null || this.endsAt.isAfter(Instant.now())) {
            this.endsAt = Instant.now();
        }

        this.status = EventStatusEnum.FINISHED;
    }

    /**
     * Valida a disponibilidade de vagas e a permissão de ingresso no evento.
     * <p>
     * Retorna verdadeiro apenas se o evento estiver configurado para aceitar
     * participantes e o total de inscritos ativos for estritamente menor que a
     * capacidade máxima estabelecida.
     * </p>
     * <p>
     * A precisão desta verificação depende de um contador de voluntários
     * consistente, preferencialmente obtido via lock pessimista para evitar
     * violações de limite em processamentos paralelos.
     * </p>
     * 
     * @param activeCount Total de inscrições com status confirmado.
     * 
     * @return Estado da disponibilidade de vagas para novos voluntários.
     */
    public boolean acceptsNewVolunteers(long activeCount) {
        return this.acceptsVolunteers && activeCount < this.maxVolunteers;
    }

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
