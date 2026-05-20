package com.techfun.altrua.features.user.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.InvalidCredentialsException;
import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.user.api.UserMapper;
import com.techfun.altrua.features.user.api.dto.ChangeEmailRequestDTO;
import com.techfun.altrua.features.user.api.dto.ChangePasswordRequestDTO;
import com.techfun.altrua.features.user.api.dto.UpdateUserRequestDTO;
import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.model.User;
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
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Atualiza os dados do perfil do usuário autenticado.
     *
     * <p>
     * Apenas os campos {@code name} e {@code avatarUrl} são atualizáveis.
     * Campos nulos no DTO são ignorados, permitindo atualizações parciais.
     * </p>
     *
     * @param dto dados a serem atualizados.
     * @return {@link UserResponseDTO} com o perfil atualizado.
     * @throws ResourceNotFoundException se o usuário não for encontrado.
     */
    @Transactional
    public UserResponseDTO update(UpdateUserRequestDTO dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário"));

        if (dto.name() != null)
            user.setName(dto.name());
        if (dto.avatarUrl() != null)
            user.setAvatarUrl(dto.avatarUrl());

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    /**
     * Altera a senha do usuário autenticado.
     *
     * <p>
     * A senha atual é validada antes da alteração. Caso não corresponda
     * à senha cadastrada, uma exceção de credenciais inválidas é lançada.
     * </p>
     *
     * @param dto contendo a senha atual e a nova senha.
     * @throws ResourceNotFoundException   se o usuário não for encontrado.
     * @throws InvalidCredentialsException se a senha atual estiver incorreta.
     */
    @Transactional
    public void changePassword(ChangePasswordRequestDTO dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
    }

    /**
     * Altera o e-mail do usuário autenticado.
     *
     * <p>
     * A senha atual é validada antes da alteração. Caso não corresponda
     * à senha cadastrada, uma exceção de credenciais inválidas é lançada.
     * </p>
     *
     * @param dto contendo a senha atual e o novo e-mail.
     * @throws ResourceNotFoundException   se o usuário não for encontrado.
     * @throws InvalidCredentialsException se a senha atual estiver incorreta.
     */
    @Transactional
    public void changeEmail(ChangeEmailRequestDTO dto) {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário"));

        if (!passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        user.setEmail(dto.newEmail());
        userRepository.save(user);
    }

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
     * @throws ResourceNotFoundException se o identificador do usuário no contexto
     *                                   não existir na base.
     */
    public UserResponseDTO getMe() {
        UUID userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário"));
        return userMapper.toDTO(user);
    }
}
