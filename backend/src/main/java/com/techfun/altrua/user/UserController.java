package com.techfun.altrua.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.security.userdetails.UserPrincipal;
import com.techfun.altrua.user.dto.UserResponseDTO;

import lombok.RequiredArgsConstructor;

/**
 * Controller responsável pelos endpoints relacionados aos usuários.
 *
 * <p>
 * Expõe operações sobre o recurso {@link User} sob o prefixo {@code /users}.
 * </p>
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retorna os dados do usuário autenticado.
     *
     * <p>
     * Extrai o usuário diretamente do {@link UserPrincipal} populado pelo filtro
     * JWT,
     * evitando consulta adicional ao banco de dados.
     * </p>
     *
     * @param userPrincipal o principal do usuário autenticado extraído do contexto
     *                      de segurança
     * @return {@link ResponseEntity} contendo os dados do usuário sem a senha
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(userService.getMe(userPrincipal.getUser()));
    }

}
