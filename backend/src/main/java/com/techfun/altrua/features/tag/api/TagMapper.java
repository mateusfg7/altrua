package com.techfun.altrua.features.tag.api;

import org.mapstruct.Mapper;

import com.techfun.altrua.features.tag.api.dto.TagResponseDTO;
import com.techfun.altrua.features.tag.domain.Tag;

/**
 * Mapper responsável pela conversão da entidade {@link Tag} para seus objetos
 * de transferência de dados.
 * <p>
 * Atua na simplificação do modelo de domínio para a camada de apresentação,
 * garantindo que apenas os metadados necessários das etiquetas sejam expostos.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface TagMapper {

    /**
     * Converte uma entidade {@link Tag} em um DTO de resposta.
     * <p>
     * Este método é fundamental para fornecer representações leves de categorias
     * em listagens de eventos ou filtros de busca na API.
     * </p>
     *
     * @param tag A entidade persistida contendo os dados da etiqueta.
     * @return {@link TagResponseDTO} contendo o identificador e o nome amigável da
     *         tag.
     */
    TagResponseDTO toDto(Tag tag);
}
