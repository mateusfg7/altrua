package com.techfun.altrua.features.event.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.EventVolunteer;

import jakarta.persistence.LockModeType;

/**
 * Interface de persistência para a entidade EventVolunteer, fornecendo métodos
 * de consulta otimizados e controle de concorrência.
 */
public interface EventVolunteerRepository extends JpaRepository<EventVolunteer, UUID> {

    /**
     * Contabiliza o total de inscrições para um evento específico filtrando pelo
     * status informado.
     * <p>
     * Utilizado para validar o limite de vagas (quando o status é CONFIRMED) ou
     * para métricas de cancelamento antes de processar novas operações de
     * inscrição.
     * </p>
     * 
     * @param eventId Identificador único do evento.
     * @param status  Estado da inscrição a ser contabilizado (ex: CONFIRMED,
     *                CANCELLED).
     * @return Quantidade total de voluntários que atendem aos critérios de filtro.
     */
    @Query("SELECT COUNT(ev) FROM EventVolunteer ev WHERE ev.event.id = :eventId AND ev.status = :status")
    public long countByEventIdAndStatus(@Param("eventId") UUID eventId, @Param("status") VolunteerStatusEnum status);

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

    /**
     * Calcula a quantidade de voluntários filtrados por status para uma lista
     * específica de eventos.
     * <p>
     * O uso de parâmetros para o status evita problemas de incompatibilidade de
     * tipos
     * entre o Enum Java e o literal do banco de dados, além de facilitar a
     * manutenção.
     * </p>
     * 
     * @param ids    Lista de {@link UUID} dos eventos que serão contabilizados.
     * @param status O {@link VolunteerStatusEnum} para filtrar os voluntários (ex:
     *               CONFIRMED).
     * @return Uma {@link List} de {@code Object[]}, onde cada elemento do array
     *         contém:
     *         <ul>
     *         <li>{@code [0]} ({@link UUID}): O identificador único do evento.</li>
     *         <li>{@code [1]} ({@link Long}): A contagem total de voluntários com o
     *         status informado.</li>
     *         </ul>
     */
    @Query("SELECT ev.event.id, COUNT(ev) FROM EventVolunteer ev WHERE ev.event.id IN :ids AND ev.status = :status GROUP BY ev.event.id")
    List<Object[]> countVolunteersByEventIdsAndStatus(
            @Param("ids") List<UUID> ids,
            @Param("status") VolunteerStatusEnum status);
}