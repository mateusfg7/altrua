package com.techfun.altrua.features.ong.api.dto;

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
public record OngFilterDTO(String name, String slug) {
}
