package com.techfun.altrua.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techfun.altrua.dto.auth.AuthResponseDTO;
import com.techfun.altrua.dto.auth.LoginRequestDTO;
import com.techfun.altrua.dto.auth.RegisterRequestDTO;
import com.techfun.altrua.entities.RefreshToken;
import com.techfun.altrua.entities.User;
import com.techfun.altrua.exceptions.EmailAlreadyInUseException;
import com.techfun.altrua.exceptions.InvalidCredentialsException;
import com.techfun.altrua.repository.UserRepository;
import com.techfun.altrua.security.jwt.JwtProvider;
import com.techfun.altrua.security.userdetails.UserPrincipal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelas regras de negócio relacionadas à autenticação
 * e registro de usuários.
 *
 * <p>
 * Orquestra o fluxo completo de autenticação — registro, login, renovação
 * de token e logout — delegando responsabilidades específicas para
 * {@link RefreshTokenService} e {@link JwtProvider}.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra um novo usuário no sistema.
     *
     * <p>
     * Verifica se o e-mail já está em uso antes de criar o usuário.
     * A senha fornecida é criptografada antes de ser persistida.
     * Caso haja violação de integridade no banco (race condition), a exceção
     * é tratada e convertida para {@link EmailAlreadyInUseException}.
     * </p>
     *
     * @param dto objeto contendo os dados do registro (nome, e-mail e senha)
     * @return {@link AuthResponseDTO} contendo o access token e refresh token
     *         gerados
     * @throws EmailAlreadyInUseException se o e-mail informado já estiver
     *                                    cadastrado
     */
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyInUseException();
        }

        try {
            User user = User.create(
                    dto.getName(),
                    dto.getEmail(),
                    passwordEncoder.encode(dto.getPassword()));

            userRepository.save(user);

            String token = jwtProvider.generateToken(new UserPrincipal(user));
            RefreshToken refreshToken = refreshTokenService.create(user);

            return new AuthResponseDTO(token, refreshToken.getToken());
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyInUseException();
        }
    }

    /**
     * Realiza a autenticação do usuário com base nas credenciais fornecidas.
     *
     * <p>
     * Utiliza o {@link AuthenticationManager} para validar e-mail e senha.
     * Em caso de sucesso, gera e retorna um par de tokens encapsulado em
     * {@link AuthResponseDTO}.
     * </p>
     *
     * @param dto objeto contendo as credenciais de acesso (e-mail e senha)
     * @return {@link AuthResponseDTO} contendo o access token e refresh token
     *         gerados
     * @throws InvalidCredentialsException se a autenticação falhar por credenciais
     *                                     inválidas
     */
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtProvider.generateToken(userPrincipal);
            RefreshToken refreshToken = refreshTokenService.create(userPrincipal.getUser());

            return new AuthResponseDTO(token, refreshToken.getToken());
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Renova o access token a partir de um refresh token válido.
     *
     * <p>
     * O refresh token atual é rotacionado — invalidado e substituído por um novo.
     * O cliente deve armazenar ambos os tokens retornados.
     * </p>
     *
     * @param token o refresh token a ser utilizado na renovação
     * @return {@link AuthResponseDTO} contendo o novo access token e novo refresh
     *         token
     * @throws RefreshTokenException se o token for inválido, revogado ou expirado
     */
    @Transactional
    public AuthResponseDTO refresh(String token) {
        RefreshToken refreshToken = refreshTokenService.rotate(token);

        UserPrincipal userPrincipal = new UserPrincipal(refreshToken.getUser());
        String newToken = jwtProvider.generateToken(userPrincipal);

        return new AuthResponseDTO(newToken, refreshToken.getToken());
    }

    /**
     * Encerra a sessão do usuário revogando o refresh token informado.
     *
     * <p>
     * Após o logout, o refresh token não poderá ser utilizado para
     * renovar o access token.
     * </p>
     *
     * @param token o refresh token a ser revogado
     * @throws RefreshTokenException se o token não for encontrado
     */
    @Transactional
    public void logout(String token) {
        refreshTokenService.revoke(token);
    }
}