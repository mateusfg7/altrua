package com.techfun.altrua.auth.refresh;

import com.techfun.altrua.user.User;

/**
 * Resultado da operação de rotação de refresh token.
 *
 * <p>
 * Encapsula o novo refresh token gerado e o usuário vinculado,
 * permitindo que o {@link com.techfun.altrua.auth.AuthService}
 * gere um novo access token sem consultas adicionais ao banco.
 * </p>
 *
 * @param newToken novo refresh token gerado em formato JWT original
 * @param user     usuário vinculado ao token rotacionado
 */
public record RotateResult(String newToken, User user) {
}
