package com.techfun.altrua.features.ong.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Objeto de Transferência de Dados (DTO) utilizado para encapsular os
 * parâmetros de filtragem de ONGs.
 * <p>
 * Este record é utilizado principalmente na camada de controle para capturar
 * parâmetros de busca
 * da URL e transportá-los até a camada de especificação
 * ({@code OngSpecification}).
 * </p>
 *
 * @param name O nome ou parte do nome da ONG para busca textual (filtro
 *             parcial).
 * @param slug O identificador amigável e único da ONG (filtro exato).
 */
@Schema(description = "Parâmetros para filtragem de organizações")
public record OngFilterDTO(

        @Schema(description = "Nome ou parte do nome da ONG para busca textual. A busca é case-insensitive.", example = "Amigos dos Animais") String name,

        @Schema(description = "Identificador amigável e único da ONG. A busca exige correspondência exata.", example = "amigos-dos-animais") String slug) {
}
