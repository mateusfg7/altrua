package com.techfun.altrua.features.auth.domain;

import com.techfun.altrua.features.user.domain.User;

/**
 * Resultado da operação de rotação de refresh token.
 *
 * <p>
 * Encapsula o novo refresh token gerado e o usuário vinculado,
 * permitindo que o {@link com.techfun.altrua.features.auth.service.AuthService}
 * gere um novo access token sem consultas adicionais ao banco.
 * </p>
 *
 * @param newToken novo refresh token gerado em formato JWT original
 * @param user     usuário vinculado ao token rotacionado
 */
public record RotateResult(String newToken, User user) {
}
