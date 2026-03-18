package com.techfun.altrua.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techfun.altrua.dto.auth.AuthResponseDTO;
import com.techfun.altrua.dto.auth.RegisterRequestDTO;
import com.techfun.altrua.entities.User;
import com.techfun.altrua.exceptions.EmailAlreadyInUseException;
import com.techfun.altrua.repository.UserRepository;
import com.techfun.altrua.security.jwt.JwtProvider;
import com.techfun.altrua.security.userdetails.UserPrincipal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelas regras de negócio relacionadas à autenticação e
 * registro de usuários.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * Registra um novo usuário no sistema.
     *
     * <p>
     * Verifica se o e-mail já está em uso antes de criar o usuário.
     * A senha fornecida é criptografada antes de ser salva.
     * </p>
     *
     * @param dto objeto contendo os dados do registro (nome, e-mail e senha)
     * @return objeto contendo o token JWT gerado para o novo usuário
     * @throws EmailAlreadyInUseException se o e-mail informado já estiver cadastrado
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyInUseException();
        }

        // TO DO: Criar tratamento para evitar condições de corrida (duas requisições ao
        // mesmo tempo)
        User user = User.create(
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()));

        userRepository.save(user);

        String token = jwtProvider.generateToken(new UserPrincipal(user));
        return new AuthResponseDTO(token);
    }

}
