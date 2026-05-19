package com.techfun.altrua.features.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.user.api.dto.ChangeEmailRequestDTO;
import com.techfun.altrua.features.user.api.dto.ChangePasswordRequestDTO;
import com.techfun.altrua.features.user.api.dto.UpdateUserRequestDTO;
import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller responsável pelos endpoints relacionados aos usuários.
 *
 * <p>
 * Expõe operações sobre o recurso {@link User} sob o prefixo {@code /users}.
 * </p>
 */
@Tag(name = "Usuários", description = "Gerenciamento do perfil do usuário autenticado")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Atualiza parcialmente o perfil do usuário autenticado.
     *
     * <p>
     * Apenas os campos informados serão atualizados. Campos nulos são ignorados.
     * </p>
     *
     * @param dto dados a serem atualizados.
     * @return {@link UserResponseDTO} com o perfil atualizado.
     */
    @Operation(summary = "Atualizar perfil", description = "Atualiza parcialmente o perfil do usuário autenticado. Apenas os campos informados serão alterados")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PatchMapping("/me")
    public ResponseEntity<UserResponseDTO> update(@RequestBody @Valid UpdateUserRequestDTO dto) {
        UserResponseDTO updatedUser = userService.update(dto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Altera o e-mail do usuário autenticado.
     *
     * <p>
     * A senha atual é exigida para confirmar a identidade do usuário
     * antes de aplicar a alteração.
     * </p>
     *
     * @param dto contendo a senha atual e o novo e-mail.
     */
    @Operation(summary = "Alterar e-mail", description = "Altera o e-mail do usuário autenticado. A senha atual é exigida para confirmar a identidade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "E-mail alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Senha atual incorreta")
    })
    @PatchMapping("/me/email")
    public ResponseEntity<Void> changeEmail(@RequestBody @Valid ChangeEmailRequestDTO dto) {
        userService.changeEmail(dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Altera a senha do usuário autenticado.
     *
     * <p>
     * A senha atual é exigida para confirmar a identidade do usuário
     * antes de aplicar a alteração.
     * </p>
     *
     * @param dto contendo a senha atual e a nova senha.
     */
    @Operation(summary = "Alterar senha", description = "Altera a senha do usuário autenticado. A senha atual é exigida para confirmar a identidade")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Senha atual incorreta")
    })
    @PatchMapping("/me/password")
    public ResponseEntity<Void> changePassword(@RequestBody @Valid ChangePasswordRequestDTO dto) {
        userService.changePassword(dto);
        return ResponseEntity.noContent().build();
    }

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
    @Operation(summary = "Obter perfil", description = "Retorna os dados atualizados do perfil do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        return ResponseEntity.ok(userService.getMe());
    }

}
