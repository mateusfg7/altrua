package com.techfun.altrua.features.auth.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.auth.api.dto.AuthResponseDTO;
import com.techfun.altrua.features.auth.api.dto.LoginRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RefreshTokenRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RegisterUserRequestDTO;
import com.techfun.altrua.features.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável pelos endpoints de autenticação e gestão de
 * sessão.
 *
 * <p>
 * Os endpoints de registro (signup) e autenticação (login) são públicos.
 * Os endpoints de renovação de token (refresh) e encerramento de sessão
 * (logout)
 * exigem autenticação e o envio dos tokens correspondentes para processamento.
 * </p>
 */
@Tag(name = "Autenticação", description = "Login, registro e gerenciamento de sessão")
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
    @Operation(summary = "Registrar usuário", description = "Cria um novo usuário e retorna um par de tokens para início imediato da sessão")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterUserRequestDTO dto) {
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
    @Operation(summary = "Realizar login", description = "Valida as credenciais e retorna um par de tokens para uso nas requisições autenticadas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
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
    @Operation(summary = "Renovar tokens", description = "Invalida o refresh token atual e retorna um novo par de tokens (token rotation). Substitua ambos os tokens armazenados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
    })
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
    @Operation(summary = "Encerrar sessão", description = "Revoga o refresh token informado. Descarte ambos os tokens armazenados localmente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sessão encerrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid RefreshTokenRequestDTO dto) {
        authService.logout(dto.getToken());
        return ResponseEntity.noContent().build();
    }
}
