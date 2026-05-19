package com.techfun.altrua.integration.features.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techfun.altrua.features.auth.api.AuthController;
import com.techfun.altrua.features.auth.api.dto.AuthResponseDTO;
import com.techfun.altrua.features.auth.api.dto.LoginRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RefreshTokenRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RegisterUserRequestDTO;
import com.techfun.altrua.features.auth.service.AuthService;
import com.techfun.altrua.infra.config.handler.GlobalExceptionHandler;
import com.techfun.altrua.integration.BaseControllerTest;

/**
 * Testes de integração para os endpoints de Autenticação.
 * Verifica o processamento de tokens, validações de entrada e contratos de API,
 * operando com filtros de segurança desabilitados para isolamento do
 * controlador.
 */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("API: AuthController")
class AuthControllerIT extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    private final AuthResponseDTO mockAuthResponse = new AuthResponseDTO("access-token-valido", "refresh-token-valido");

    @Nested
    @DisplayName("POST /auth/signup")
    class Signup {

        /**
         * Verifica se o endpoint de registro processa payloads válidos,
         * retornando os tokens de acesso com o status HTTP correto de criação.
         */
        @Test
        @DisplayName("deve retornar 201 Created e os tokens ao registrar um usuário válido")
        void shouldRegisterUserAndReturn201WhenPayloadIsValid() throws Exception {
            RegisterUserRequestDTO request = new RegisterUserRequestDTO("creator@email.com", "senha123", "Creator");

            when(authService.register(any(RegisterUserRequestDTO.class))).thenReturn(mockAuthResponse);

            mockMvc.perform(post("/auth/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").value("access-token-valido"))
                    .andExpect(jsonPath("$.refreshToken").value("refresh-token-valido"));
        }
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        /**
         * Garante que o endpoint de login valida credenciais corretas,
         * respondendo com status 200 e emitindo o par de tokens correspondente.
         */
        @Test
        @DisplayName("deve retornar 200 OK e os tokens ao realizar login com credenciais válidas")
        void shouldAuthenticateAndReturn200WhenCredentialsAreValid() throws Exception {
            LoginRequestDTO request = new LoginRequestDTO("creator@email.com", "senha123");

            when(authService.login(any(LoginRequestDTO.class))).thenReturn(mockAuthResponse);

            mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-token-valido"))
                    .andExpect(jsonPath("$.refreshToken").value("refresh-token-valido"));
        }
    }

    @Nested
    @DisplayName("POST /auth/refresh")
    class Refresh {

        /**
         * Valida o fluxo de rotação de chaves (token rotation), garantindo a emissão
         * de novos tokens mediante o envio de um refresh token ativo.
         */
        @Test
        @DisplayName("deve retornar 200 OK e o novo par de tokens ao renovar a sessão")
        void shouldRotateTokensAndReturn200WhenRefreshTokenIsValid() throws Exception {
            RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("refresh-token-valido");

            when(authService.refresh("refresh-token-valido")).thenReturn(mockAuthResponse);

            mockMvc.perform(post("/auth/refresh")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").value("access-token-valido"));
        }
    }

    @Nested
    @DisplayName("POST /auth/logout")
    class Logout {

        /**
         * Confirma o encerramento da sessão do usuário, assegurando o retorno de status
         * 204 após a invalidação lógica do token de renovação informado.
         */
        @Test
        @DisplayName("deve retornar 204 No Content ao encerrar a sessão com sucesso")
        void shouldInvalidateSessionAndReturn204WhenLogoutIsSuccessful() throws Exception {
            RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("refresh-token-valido");

            doNothing().when(authService).logout("refresh-token-valido");

            mockMvc.perform(post("/auth/logout")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());
        }
    }
}