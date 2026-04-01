package com.techfun.altrua.features.event.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techfun.altrua.features.event.domain.model.Tag;

/**
 * Repositório para operações de persistência da entidade {@link Tag}.
 * 
 * <p>
 * Gerencia o armazenamento e a consulta de etiquetas utilizadas para
 * a categorização e busca de eventos no sistema.
 * </p>
 */
public interface TagRepository extends JpaRepository<Tag, UUID> {

    /**
     * Recupera uma lista de tags cujos nomes estejam contidos no conjunto
     * fornecido.
     * 
     * <p>
     * Esta consulta é fundamental para o processo de verificação de existência
     * antes da criação de novas tags em lote.
     * </p>
     *
     * @param names Conjunto de nomes (normalizados) para filtragem.
     * @return Lista de entidades {@link Tag} encontradas.
     */
    public List<Tag> findAllByNameIn(Set<String> names);
}
