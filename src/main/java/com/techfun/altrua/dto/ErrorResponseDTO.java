package com.techfun.altrua.dto;

import java.time.Instant;

import org.springframework.http.HttpStatus;

/**
 * Objeto de Transferência de Dados (DTO) para padronização de respostas de erro.
 *
 * <p>
 * Utilizado pelo tratamento global de exceções para retornar informações
 * consistentes ao cliente da API quando ocorre uma falha.
 * </p>
 *
 * @param message   mensagem descritiva do erro
 * @param status    código numérico do status HTTP
 * @param path      caminho da URI onde o erro ocorreu
 * @param timestamp data e hora do erro
 */
public record ErrorResponseDTO(String message, int status, String path, Instant timestamp) {

    /**
     * Cria uma instância de erro com o timestamp atual.
     *
     * @param message mensagem descritiva do erro
     * @param status  status HTTP associado ao erro
     * @param path    caminho da requisição
     * @return nova instância configurada com o instante atual
     */
    public static ErrorResponseDTO of(String message, HttpStatus status, String path) {
        return new ErrorResponseDTO(message, status.value(), path, Instant.now());
    }
}
