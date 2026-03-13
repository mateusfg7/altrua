package com.techfun.altrua.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.dto.RegisterRequestDTO;
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
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody @Valid RegisterRequestDTO dto) {
        authService.register(dto);
    }
}
