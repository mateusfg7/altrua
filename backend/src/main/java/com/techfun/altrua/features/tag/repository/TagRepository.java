package com.techfun.altrua.features.tag.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.techfun.altrua.features.tag.domain.Tag;

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

    /**
     * Recupera todas as etiquetas cadastradas no sistema em ordem alfabética.
     * <p>
     * Este método é utilizado principalmente para alimentar componentes de
     * interface (filtros),
     * garantindo uma experiência de busca visual consistente para o usuário.
     * </p>
     *
     * @return Uma {@link List} contendo todas as instâncias de {@link Tag},
     *         ordenadas de forma ascendente (A-Z) pelo atributo {@code name}.
     */
    public List<Tag> findAllByOrderByNameAsc();

    /**
     * Realiza a persistência em lote de tags de forma idempotente.
     * *
     * <p>
     * Utiliza a cláusula {@code ON CONFLICT (name) DO NOTHING} para ignorar nomes
     * já existentes,
     * prevenindo exceções de violação de restrição de unicidade em operações
     * concorrentes.
     * </p>
     * *
     * <p>
     * <strong>Nota de Arquitetura:</strong> Como esta é uma {@code nativeQuery}, a
     * geração do
     * identificador (UUID v7) é delegada exclusivamente à estratégia de
     * {@code DEFAULT} da coluna
     * no PostgreSQL, ignorando as anotações {@code @GeneratedValue} do Hibernate.
     * </p>
     * *
     * <p>
     * O uso de {@code clearAutomatically = true} é obrigatório para sincronizar o
     * estado
     * do Persistence Context, invalidando entidades previamente carregadas que
     * possam ter sido
     * afetadas pela alteração direta no banco.
     * </p>
     *
     * @param names Array de strings contendo os nomes das tags a serem asseguradas.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO tags (name) SELECT unnest(cast(:names as text[])) ON CONFLICT (name) DO NOTHING", nativeQuery = true)
    public void ensureTagsExist(@Param("names") String[] names);
}
