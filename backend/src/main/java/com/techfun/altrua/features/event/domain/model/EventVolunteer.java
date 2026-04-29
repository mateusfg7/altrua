package com.techfun.altrua.features.event.domain.model;

import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.user.domain.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representa o vínculo de inscrição entre um voluntário e um evento específico.
 * <p>
 * Esta entidade gerencia o ciclo de vida da participação, permitindo o controle
 * de estados entre confirmação e cancelamento, além de registrar carimbos de
 * data/hora para auditoria. A integridade das inscrições duplicadas é garantida
 * por restrições de unicidade no banco de dados entre as colunas de evento e
 * usuário.
 * </p>
 */
@Entity
@Table(name = "event_volunteers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventVolunteer {

    /** Identificador único da inscrição no formato UUID. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Referência ao evento que disponibiliza a vaga. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, updatable = false)
    private Event event;

    /** Usuário voluntário vinculado à inscrição. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    /** Estado atual da participação (ex: CONFIRMED, CANCELLED). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VolunteerStatusEnum status;

    /** Instante da última confirmação ou reativação da inscrição. */
    @Column(name = "confirmed_at", nullable = false)
    private Instant confirmedAt;

    /** Registro temporal de quando o usuário solicitou o cancelamento. */
    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    private EventVolunteer(Event event, User user) {
        this.event = event;
        this.user = user;
        this.status = VolunteerStatusEnum.CONFIRMED;
    }

    /**
     * Cria uma nova instância de inscrição com o estado inicial confirmado.
     * 
     * @param event O evento ao qual o voluntário está se vinculando.
     * @param user  O usuário que está realizando a inscrição.
     * @return Uma nova instância de EventVolunteer configurada.
     */
    public static EventVolunteer enroll(Event event, User user) {
        return new EventVolunteer(event, user);
    }

    /**
     * Altera o status da inscrição para cancelado e registra o instante da
     * operação.
     */
    public void cancel() {
        this.status = VolunteerStatusEnum.CANCELLED;
        this.cancelledAt = Instant.now();
    }

    /**
     * Reverte um cancelamento anterior, restaurando o status para confirmado e
     * removendo a data de cancelamento.
     */
    public void reactivate() {
        this.status = VolunteerStatusEnum.CONFIRMED;
        this.cancelledAt = null;
    }

    /**
     * Define o instante de confirmação do registro no momento da persistência
     * inicial.
     */
    @PrePersist
    private void onPersist() {
        this.confirmedAt = Instant.now();
    }

    /**
     * Atualiza o carimbo de confirmação apenas quando o status for CONFIRMED,
     * evitando que operações de cancelamento sobrescrevam o instante
     * da última confirmação ou reativação.
     */
    @PreUpdate
    private void onUpdate() {
        if (this.status == VolunteerStatusEnum.CONFIRMED) {
            this.confirmedAt = Instant.now();
        }
    }
}
