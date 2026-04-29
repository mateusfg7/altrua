package com.techfun.altrua.features.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.service.UserService;

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
     * Obtém as informações do perfil do usuário autenticado.
     *
     * <p>
     * O identificador é extraído do contexto de segurança atual. Realiza uma
     * consulta ao banco de dados para garantir que as informações retornadas
     * estejam sincronizadas com o estado mais recente do registro.
     * </p>
     * * @return {@link UserResponseDTO} contendo os detalhes do perfil do usuário.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getMe());
    }

}
