package com.techfun.altrua.features.user.service;

import org.springframework.stereotype.Service;

import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.User;

/**
 * Serviço responsável pelas regras de negócio relacionadas aos usuários.
 */
@Service
public class UserService {

    /**
     * Retorna os dados do usuário autenticado.
     *
     * <p>
     * Converte a entidade {@link User} para {@link UserResponseDTO},
     * sem realizar consulta adicional ao banco de dados.
     * </p>
     *
     * @param user a entidade do usuário autenticado
     * @return {@link UserResponseDTO} com os dados públicos do usuário
     */
    public UserResponseDTO getMe(User user) {
        return new UserResponseDTO(user);
    }

}
