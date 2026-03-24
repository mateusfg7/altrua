package com.techfun.altrua.auth.dto;

/**
 * DTO de resposta para operações de autenticação.
 *
 * <p>
 * Contém os tokens necessários para o cliente
 * realizar requisições autenticadas e renovar a sessão.
 * </p>
 *
 * @param accessToken  token JWT de curta duração utilizado para autenticar
 *                     requisições
 * @param refreshToken token JWT de longa duração utilizado para renovar o
 *                     access token
 */
public record AuthResponseDTO(String accessToken, String refreshToken) {

}
