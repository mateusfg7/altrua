package com.techfun.altrua.features.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.InvalidCredentialsException;
import com.techfun.altrua.core.common.exceptions.RefreshTokenException;
import com.techfun.altrua.features.auth.api.dto.AuthResponseDTO;
import com.techfun.altrua.features.auth.api.dto.LoginRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RegisterUserRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RotateResult;
import com.techfun.altrua.features.user.domain.User;
import com.techfun.altrua.features.user.repository.UserRepository;
import com.techfun.altrua.infra.security.jwt.JwtProvider;
import com.techfun.altrua.infra.security.jwt.JwtValidator;
import com.techfun.altrua.infra.security.userdetails.UserPrincipal;

import io.jsonwebtoken.JwtException;
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
    private final JwtValidator jwtValidator;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra um novo usuário no sistema.
     *
     * <p>
     * Verifica se o e-mail já está em uso antes de criar o usuário.
     * A senha fornecida é criptografada antes de ser persistida.
     * Caso haja violação de integridade no banco (race condition), a exceção
     * é tratada e convertida para {@link DuplicateResourceException}.
     * </p>
     *
     * @param dto objeto contendo os dados do registro (nome, e-mail e senha)
     * @return {@link AuthResponseDTO} contendo o access token e refresh token
     *         gerados
     * @throws DuplicateResourceException se o e-mail informado já estiver
     *                                    cadastrado
     * @throws RefreshTokenException      se houver erro ao gerar ou persistir o
     *                                    refresh token
     */
    @Transactional
    public AuthResponseDTO register(RegisterUserRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("E-mail já cadastrado");
        }

        try {
            User user = User.create(
                    dto.getName(),
                    dto.getEmail(),
                    passwordEncoder.encode(dto.getPassword()));

            userRepository.saveAndFlush(user);

            String token = jwtProvider.generateToken(new UserPrincipal(user));
            String refreshToken = refreshTokenService.create(user);

            return new AuthResponseDTO(token, refreshToken);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("E-mail já cadastrado");
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
     * @throws RefreshTokenException       se houver erro ao gerar ou persistir o
     *                                     refresh token
     */
    @Transactional
    public AuthResponseDTO login(LoginRequestDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtProvider.generateToken(userPrincipal);
            String refreshToken = refreshTokenService.create(userPrincipal.getUser());

            return new AuthResponseDTO(token, refreshToken);
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Renova o par de tokens (Access e Refresh) a partir de um Refresh Token
     * válido.
     *
     * <p>
     * Esta operação realiza as seguintes etapas:
     * 1. Valida a integridade estrutural, assinatura e expiração do token via
     * {@link JwtValidator#validateTokenIntegrity(String)}.
     * 2. Verifica se o token possui o claim específico de tipo 'refresh'.
     * 3. Executa a rotação do token (invalidação do antigo e geração de um novo)
     * via {@link RefreshTokenService}.
     * 4. Gera um novo Access Token baseado no usuário recuperado da rotação.
     * </p>
     *
     * @param token O Refresh Token enviado no corpo da requisição.
     * @return {@link AuthResponseDTO} contendo o novo Access Token e o novo Refresh
     *         Token.
     * @throws RefreshTokenException Se o token for estruturalmente inválido,
     *                               expirado,
     *                               de tipo incorreto ou falhar na rotação da
     *                               persistência.
     */
    @Transactional
    public AuthResponseDTO refresh(String token) {
        try {
            jwtValidator.validateTokenIntegrity(token);

            if (!jwtValidator.isRefreshToken(token)) {
                throw new RefreshTokenException("Refresh Token inválido");
            }

            RotateResult current = refreshTokenService.rotate(token);
            String newAccessToken = jwtProvider.generateToken(new UserPrincipal(current.user()));
            return new AuthResponseDTO(newAccessToken, current.newToken());
        } catch (JwtException | IllegalArgumentException ex) {
            throw new RefreshTokenException("Refresh Token inválido ou expirado");
        }
    }

    /**
     * Encerra a sessão ativa do usuário através da revogação do Refresh Token.
     *
     * <p>
     * O processo consiste em validar a autenticidade do token antes de delegar a
     * revogação
     * à camada de persistência. Uma vez revogado, o token não poderá mais ser
     * utilizado
     * para gerar novos Access Tokens.
     * </p>
     *
     * @param token O Refresh Token a ser invalidado.
     * @throws RefreshTokenException Se o token for inválido, expirado, não for do
     *                               tipo 'refresh'
     *                               ou não for encontrado no registro de sessões
     *                               ativas.
     */
    @Transactional
    public void logout(String token) {
        try {
            jwtValidator.validateTokenIntegrity(token);

            if (!jwtValidator.isRefreshToken(token)) {
                throw new RefreshTokenException("Refresh Token inválido");
            }

            refreshTokenService.revoke(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new RefreshTokenException("Refresh Token inválido ou expirado");
        }
    }
}