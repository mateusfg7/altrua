package com.techfun.altrua.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.dto.auth.AuthResponseDTO;
import com.techfun.altrua.dto.auth.RegisterRequestDTO;
import com.techfun.altrua.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável pelos endpoints de autenticação.
 * Disponibiliza rotas para registro e operações relacionadas a acesso.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    /**
     * Endpoint para registro de novos usuários.
     *
     * @param dto dados do usuário a ser registrado, validados automaticamente
     * @return {@link ResponseEntity} contendo o token de autenticação gerado
     */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody @Valid RegisterRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

}
