package com.techfun.altrua.features.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.User;
import com.techfun.altrua.features.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelas regras de negócio relacionadas aos usuários.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * Recupera o perfil do usuário atualmente autenticado na sessão.
     *
     * <p>
     * Este método garante a integridade dos dados ao realizar uma consulta
     * atualizada ao banco de dados, evitando o uso de informações possivelmente
     * obsoletas contidas no token ou no contexto de segurança.
     * </p>
     *
     * @return {@link UserResponseDTO} contendo os dados atuais do usuário.
     * @throws DomainException se o identificador do usuário no contexto
     *                         não existir na base.
     */
    public UserResponseDTO getMe() {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new DomainException("Usuário não encontrado"));
        return UserResponseDTO.fromEntity(user);
    }
}
