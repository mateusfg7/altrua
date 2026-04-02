package com.techfun.altrua.features.event.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Executa uma inserção em lote (bulk insert) das tags fornecidas, ignorando
     * nomes duplicados.
     * *
     * <p>
     * Esta operação é atômica no nível do banco de dados e utiliza a cláusula
     * {@code ON CONFLICT DO NOTHING} para garantir idempotência em cenários
     * concorrentes,
     * evitando {@code DataIntegrityViolationException}.
     * </p>
     * *
     * <p>
     * <strong>Nota de Implementação:</strong> Utiliza funções específicas do
     * PostgreSQL
     * ({@code unnest}) para converter a coleção em linhas. O uso de
     * {@code clearAutomatically = true}
     * garante que o Persistence Context do Hibernate seja limpo após a execução,
     * evitando
     * dados obsoletos em cache.
     * </p>
     *
     * @param names Coleção de nomes de tags a serem garantidas no banco.
     */
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO tags (name) SELECT unnest(:names) ON CONFLICT (name) DO NOTHING", nativeQuery = true)
    public void ensureTagsExist(@Param("names") Collection<String> names);
}
