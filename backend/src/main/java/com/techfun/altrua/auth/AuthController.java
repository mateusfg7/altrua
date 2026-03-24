package com.techfun.altrua.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.auth.dto.AuthResponseDTO;
import com.techfun.altrua.auth.dto.LoginRequestDTO;
import com.techfun.altrua.auth.dto.RefreshTokenRequestDTO;
import com.techfun.altrua.auth.dto.RegisterRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável pelos endpoints de autenticação.
 *
 * <p>
 * Disponibiliza rotas para registro, login, renovação de token e logout.
 * Todos os endpoints são públicos e não exigem autenticação prévia.
 * </p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint para registro de novos usuários.
     *
     * <p>
     * Cria um novo usuário no sistema e retorna um par de tokens
     * para que o cliente já inicie a sessão autenticada.
     * </p>
     *
     * @param dto dados do usuário a ser registrado, validados automaticamente
     * @return {@link ResponseEntity} com status 201 e os tokens de autenticação
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    /**
     * Endpoint para autenticação de usuários.
     *
     * <p>
     * Valida as credenciais informadas e retorna um par de tokens
     * para uso nas requisições autenticadas subsequentes.
     * </p>
     *
     * @param dto objeto contendo e-mail e senha para autenticação
     * @return {@link ResponseEntity} com status 200 e os tokens de autenticação
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Endpoint para renovação do access token.
     *
     * <p>
     * Invalida o refresh token atual e retorna um novo par de tokens
     * (token rotation). O cliente deve substituir ambos os tokens armazenados.
     * </p>
     *
     * @param dto contendo o refresh token a ser utilizado na renovação
     * @return {@link ResponseEntity} com status 200 e os novos tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody @Valid RefreshTokenRequestDTO dto) {
        return ResponseEntity.ok(authService.refresh(dto.getToken()));
    }

    /**
     * Endpoint para encerramento de sessão.
     *
     * <p>
     * Revoga o refresh token informado, impedindo sua reutilização.
     * O cliente deve descartar ambos os tokens armazenados localmente.
     * </p>
     *
     * @param dto contendo o refresh token a ser revogado
     * @return {@link ResponseEntity} com status 204 sem corpo de resposta
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequestDTO dto) {
        authService.logout(dto.getToken());
        return ResponseEntity.noContent().build();
    }
}
