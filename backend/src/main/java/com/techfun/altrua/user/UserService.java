package com.techfun.altrua.user;

import org.springframework.stereotype.Service;

import com.techfun.altrua.user.dto.UserResponseDTO;

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
