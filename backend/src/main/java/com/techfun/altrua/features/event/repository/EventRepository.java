package com.techfun.altrua.features.event.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techfun.altrua.features.event.domain.model.Event;

import jakarta.persistence.LockModeType;

/**
 * Interface de repositório para a entidade {@link Event}.
 * 
 * <p>
 * Fornece métodos de abstração para operações de CRUD e consultas
 * personalizadas
 * no banco de dados utilizando Spring Data JPA.
 * </p>
 */
public interface EventRepository extends JpaRepository<Event, UUID> {

    /**
     * Verifica se já existe um evento cadastrado com o slug fornecido.
     * 
     * <p>
     * Utilizado principalmente durante o processo de criação de novos eventos
     * para garantir a unicidade de URLs amigáveis.
     * </p>
     *
     * @param slug O identificador amigável a ser verificado.
     * @return {@code true} se o slug já estiver em uso, {@code false} caso
     *         contrário.
     */
    public boolean existsBySlug(String slug);

    /**
     * Recupera o evento com carregamento antecipado (Eager Loading) da ONG e seus
     * administradores.
     * <p>
     * Utiliza {@code JOIN FETCH} para evitar o problema de N+1 e garantir que as
     * associações
     * estejam disponíveis mesmo fora da transação original, prevenindo
     * {@code LazyInitializationException}.
     * </p>
     *
     * @param eventId Identificador do evento.
     * @return {@link Optional} com o evento e associações carregadas, ou vazio se
     *         não encontrado.
     */
    @Query("SELECT e FROM Event e JOIN FETCH e.ong o LEFT JOIN FETCH o.administrators a WHERE e.id = :eventId")
    public Optional<Event> findByIdWithOngAndAdmins(@Param("eventId") UUID eventId);

    /**
     * Recupera um evento específico sob lock pessimista de escrita para garantir
     * consistência em operações concorrentes.
     * <p>
     * A consulta valida simultaneamente a existência do evento e seu vínculo com a
     * ONG informada, prevenindo acessos indevidos a recursos de outras
     * organizações. O bloqueio impede que outros processos modifiquem o evento até
     * o fim da transação atual.
     * </p>
     * * @param eventId Identificador único do evento.
     * 
     * @param ongId Identificador da ONG proprietária do evento.
     * @return Um Optional contendo o evento encontrado ou vazio caso a relação
     *         ID/ONG seja inválida.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :eventId AND e.ong.id = :ongId")
    public Optional<Event> findByIdAndOngIdForUpdate(@Param("eventId") UUID eventId, @Param("ongId") UUID ongId);
}
