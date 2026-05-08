package com.techfun.altrua.features.auth.api.dto;

import com.techfun.altrua.features.user.domain.model.User;

/**
 * Encapsulamento do resultado da operação de rotação de Refresh Token.
 * <p>
 * Este objeto é utilizado internamente para transportar o novo token gerado e
 * os dados da entidade {@link User} associada, otimizando o fluxo de
 * autenticação ao evitar buscas redundantes no banco de dados durante a emissão
 * do novo Access Token.
 * </p>
 *
 * @param newToken O novo Refresh Token gerado, já persistido e pronto para
 *                 emissão.
 * @param user     A instância da entidade {@link User} proprietária do token,
 *                 contendo as permissões necessárias para o novo contexto de
 *                 segurança.
 */
public record RotateResult(String newToken, User user) {
}
