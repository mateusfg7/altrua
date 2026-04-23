package com.techfun.altrua.features.event.api.dto;

import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para transporte de filtros de busca de eventos.
 * <p>
 * Utilizado para realizar a filtragem dinâmica na listagem de eventos,
 * permitindo buscas por categorias (tags), situação atual e disponibilidade de
 * vagas.
 * </p>
 *
 * @param tag               Nome da categoria/tag para filtrar eventos
 *                          relacionados.
 * @param status            Situação do evento (Ex: AGENDADO, FINALIZADO).
 * @param acceptsVolunteers Filtro booleano para indicar se o evento busca novos
 *                          voluntários.
 */
@Schema(description = "Critérios de filtragem para listagem de eventos")
public record EventFilterDTO(

        @Schema(description = "Nome da tag/categoria (case-insensitive)", example = "educacao") String tag,

        @Schema(description = "Status atual do evento") EventStatusEnum status,

        @Schema(description = "Se true, filtra eventos com vagas abertas para voluntariado", example = "true") Boolean acceptsVolunteers) {
}
