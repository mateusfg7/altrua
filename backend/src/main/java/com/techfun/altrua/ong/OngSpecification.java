package com.techfun.altrua.ong;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.techfun.altrua.ong.dto.OngFilterDTO;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Componente de persistência responsável pela construção de consultas dinâmicas
 * para a entidade {@link Ong} utilizando a Criteria API do JPA.
 * <p>
 * Esta classe fornece métodos estáticos para criar objetos
 * {@link Specification}
 * que permitem filtrar registros com base em múltiplos critérios opcionais.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OngSpecification {

    /**
     * Constrói uma especificação composta baseada nos filtros fornecidos no DTO.
     * <p>
     * Os filtros são aplicados de forma cumulativa (lógica AND). Se um campo no DTO
     * estiver nulo ou em branco, o critério correspondente é ignorado.
     * </p>
     *
     * @param filter DTO contendo os parâmetros de busca {@code name} e
     *               {@code slug}.
     * @return Uma instância de {@link Specification} pronta para ser usada em
     *         métodos de repositório.
     */
    public static Specification<Ong> withFilter(OngFilterDTO filter) {
        return Specification.where(withName(filter.name()))
                .and(withSlug(filter.slug()));
    }

    /**
     * Filtra ONGs cujo nome contenha a sequência de caracteres fornecida.
     * <p>
     * A busca é do tipo <b>case-insensitive</b> e utiliza wildcards (LIKE %valor%),
     * permitindo correspondências parciais em qualquer posição do nome.
     * </p>
     *
     * @param name O termo de busca para o nome da ONG.
     * @return Uma especificação para busca parcial de nome, ou {@code null} se o
     *         parâmetro for inválido.
     */
    private static Specification<Ong> withName(String name) {
        return (root, query, criteriaBuilder) -> name == null || name.isBlank() ? null
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase(Locale.ROOT) + "%");
    }

    /**
     * Filtra ONGs por uma correspondência exata do identificador amigável (slug).
     * <p>
     * Ao contrário da busca por nome, este filtro exige igualdade estrita (EQUAL),
     * garantindo que apenas a ONG com o slug específico seja retornada.
     * Converte o parâmetro para minúsculas antes da comparação.
     * </p>
     *
     * @param slug O slug exato da ONG.
     * @return Uma especificação para busca exata por slug, ou {@code null} se o
     *         parâmetro for inválido.
     */
    private static Specification<Ong> withSlug(String slug) {
        return (root, query, criteriaBuilder) -> slug == null || slug.isBlank() ? null
                : criteriaBuilder.equal(root.get("slug"), slug.toLowerCase(Locale.ROOT));
    }
}
