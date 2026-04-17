package com.techfun.altrua.features.event.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techfun.altrua.features.event.domain.model.EventVolunteer;

import jakarta.persistence.LockModeType;

/**
 * Interface de persistência para a entidade EventVolunteer, fornecendo métodos
 * de consulta otimizados e controle de concorrência.
 */
public interface EventVolunteerRepository extends JpaRepository<EventVolunteer, UUID> {

    /**
     * Contabiliza o total de inscrições confirmadas para um evento específico.
     * <p>
     * Utilizado para validar o limite de vagas antes de permitir novos ingressos no
     * sistema.
     * </p>
     * 
     * @param eventId Identificador do evento.
     * @return Quantidade total de voluntários com status CONFIRMED.
     */
    @Query("SELECT COUNT(ev) FROM EventVolunteer ev WHERE ev.event.id = :eventId AND ev.status = 'CONFIRMED'")
    public long countActiveByEventId(@Param("eventId") UUID eventId);

    /**
     * Recupera o vínculo de inscrição entre um evento e um usuário sem aplicar
     * bloqueios.
     * 
     * @param eventId Identificador do evento.
     * @param userId  Identificador do usuário voluntário.
     * @return Um Optional contendo a inscrição, se existente.
     */
    public Optional<EventVolunteer> findByEventIdAndUserId(UUID eventId, UUID userId);

    /**
     * Recupera uma inscrição específica sob lock pessimista de escrita, validando o
     * contexto da ONG proprietária.
     * <p>
     * O uso do JOIN garante que a operação de cancelamento ou alteração respeite a
     * hierarquia entre ONG e Evento, prevenindo manipulações indevidas de recursos
     * cruzados.
     * </p>
     * 
     * @param eventId Identificador do evento.
     * @param ongId   Identificador da ONG responsável pelo evento.
     * @param userId  Identificador do voluntário.
     * @return Um Optional com a inscrição encontrada sob bloqueio para atualização
     *         segura.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ev FROM EventVolunteer ev JOIN ev.event e WHERE e.id = :eventId AND e.ong.id = :ongId AND ev.user.id = :userId")
    public Optional<EventVolunteer> findByEventIdAndOngIdAndUserIdForUpdate(
            @Param("eventId") UUID eventId,
            @Param("ongId") UUID ongId,
            @Param("userId") UUID userId);
}