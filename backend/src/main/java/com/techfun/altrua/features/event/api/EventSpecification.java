package com.techfun.altrua.features.event.api;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.techfun.altrua.features.event.api.dto.EventFilterDTO;
import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.tag.domain.Tag;

import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Provedor de especificações para filtragem dinâmica de entidades
 * {@link Event}.
 * <p>
 * Esta classe utiliza a JPA Criteria API para construir predicados baseados
 * nos parâmetros fornecidos via {@link EventFilterDTO}.
 * </p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventSpecification {

    /**
     * Constrói uma especificação composta para filtrar eventos futuros.
     * 
     * @param filter DTO contendo os critérios de busca (tag, status,
     *               voluntários).
     * @return {@link Specification} combinando o filtro de data atual com os
     *         parâmetros opcionais.
     */
    public static Specification<Event> withFilter(EventFilterDTO filter) {
        return Specification.where(nextEvents())
                .and(withTag(filter.tag()))
                .and(withStatus(filter.status()))
                .and(withVolunteers(filter.acceptsVolunteers()));
    }

    /**
     * Filtra eventos que possuam uma tag específica.
     * <p>
     * Realiza um {@code JOIN} com a tabela de tags e aplica a comparação
     * em letras minúsculas. Ativa o modificador {@code DISTINCT} na query.
     * </p>
     * 
     * @param tag Nome da tag para busca.
     * @return Predicado de igualdade ou conjunção vazia se a tag for nula/vazia.
     */
    private static Specification<Event> withTag(String tag) {
        return (root, query, criteriaBuilder) -> {
            if (tag == null || tag.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            query.distinct(true);
            Join<Event, Tag> tagJoin = root.join("tags");
            return criteriaBuilder.equal(tagJoin.get("name"), tag.toLowerCase(Locale.ROOT));
        };
    }

    /**
     * Filtra eventos por seu estado atual.
     * 
     * @param status O {@link EventStatusEnum} desejado.
     * @return Predicado de igualdade para o status.
     */
    private static Specification<Event> withStatus(EventStatusEnum status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Filtra eventos baseando-se na disponibilidade de vagas para voluntários.
     * 
     * @param acceptsVolunteers Booleano indicando a necessidade de voluntários.
     * @return Predicado de igualdade para o campo {@code acceptsVolunteers}.
     */
    private static Specification<Event> withVolunteers(Boolean acceptsVolunteers) {
        return (root, query, criteriaBuilder) -> {
            if (acceptsVolunteers == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("acceptsVolunteers"), acceptsVolunteers);
        };
    }

    /**
     * Restringe a consulta a eventos cuja data de início seja maior ou igual ao
     * instante atual.
     * 
     * @return Predicado de comparação temporal com {@code CURRENT_TIMESTAMP}.
     */
    private static Specification<Event> nextEvents() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("startsAt"),
                criteriaBuilder.currentTimestamp());
    }
}
