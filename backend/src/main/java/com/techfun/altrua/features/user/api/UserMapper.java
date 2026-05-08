package com.techfun.altrua.features.user.api;

import org.mapstruct.Mapper;

import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.model.User;

/**
 * Mapper responsável pela conversão entre a entidade {@link User} e seus DTOs
 * de resposta.
 * <p>
 * Este mapper atua como uma barreira de segurança, garantindo que informações
 * sensíveis (como hashes de senha) nunca saiam da camada de serviço para a API
 * pública.
 * </p>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Converte a entidade de domínio {@link User} para um DTO de resposta.
     * <p>
     * O mapeamento é realizado de forma automática pelo MapStruct com base na
     * correspondência de nomes de campos entre {@link User} e
     * {@link UserResponseDTO}.
     * Campos não presentes no DTO, como 'password' ou 'role', são ignorados por
     * omissão.
     * </p>
     *
     * @param user A instância da entidade de usuário carregada do banco de dados.
     * @return {@link UserResponseDTO} contendo apenas os dados públicos e seguros
     *         do perfil.
     */
    UserResponseDTO toDTO(User user);

}
