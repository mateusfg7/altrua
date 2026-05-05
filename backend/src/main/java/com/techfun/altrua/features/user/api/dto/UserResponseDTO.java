package com.techfun.altrua.features.user.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.user.domain.model.User;

/**
 * DTO de resposta com os dados públicos do usuário.
 *
 * <p>
 * Expõe apenas os campos necessários, omitindo informações sensíveis como a
 * senha.
 * </p>
 *
 * @param id        identificador único do usuário
 * @param name      nome do usuário
 * @param email     endereço de e-mail do usuário
 * @param avatarUrl URL do avatar do usuário
 * @param createdAt data e hora de criação do registro
 * @param updatedAt data e hora da última atualização do registro
 */
public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String avatarUrl,
        Instant createdAt,
        Instant updatedAt) {

    /**
     * Converte uma instância da entidade {@link User} para {@link UserResponseDTO}.
     *
     * @param user a entidade original proveniente do banco de dados
     * @return uma nova instância de DTO com os dados mapeados
     */
    public static UserResponseDTO fromEntity(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAvatarUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

}