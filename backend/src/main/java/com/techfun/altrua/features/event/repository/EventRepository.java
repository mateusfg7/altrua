package com.techfun.altrua.features.event.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techfun.altrua.features.event.domain.model.Event;

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
     * Verifica se um usuário possui privilégios administrativos sobre um evento
     * específico.
     * * @param eventId Identificador do evento.
     * 
     * @param userId Identificador do usuário.
     * @return {@code true} se o usuário for administrador da ONG responsável pelo
     *         evento.
     */
    public boolean existsByIdAndOng_Administrators_User_Id(UUID eventId, UUID userId);
}
