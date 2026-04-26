package com.techfun.altrua.features.tag.api.dto;

import java.util.UUID;

import com.techfun.altrua.features.tag.domain.Tag;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para representação simplificada de etiquetas
 * (tags).
 * <p>
 * Utilizado para transmitir metadados de categorização de eventos, servindo
 * como
 * base para a construção de filtros na interface do usuário.
 * </p>
 *
 * @param id   Identificador único da tag (necessário para persistência de
 *             filtros).
 * @param name Nome normalizado da tag (utilizado como chave de referência).
 */
@Schema(description = "Objeto de resposta para representação de etiquetas (tags)")
public record TagResponseDTO(
        @Schema(description = "Identificador único da etiqueta", example = "550e8400-e29b-411d-a716-446655440000") UUID id,

        @Schema(description = "Nome da etiqueta em formato normalizado", example = "meio ambiente") String name) {

    /**
     * Mapeia uma entidade {@link Tag} para {@link TagResponseDTO}.
     *
     * @param tag A entidade de origem vinda do banco de dados.
     * @return Uma nova instância de {@link TagResponseDTO}.
     */
    public static TagResponseDTO fromEntity(Tag tag) {
        return new TagResponseDTO(
                tag.getId(),
                tag.getName());
    }
}
