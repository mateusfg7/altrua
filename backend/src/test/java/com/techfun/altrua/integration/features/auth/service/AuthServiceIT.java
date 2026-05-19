package com.techfun.altrua.integration.features.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techfun.altrua.features.auth.api.dto.LoginRequestDTO;
import com.techfun.altrua.features.auth.api.dto.RegisterUserRequestDTO;
import com.techfun.altrua.features.auth.repository.RefreshTokenRepository;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.repository.UserRepository;
import com.techfun.altrua.integration.IntegrationTestBase;

/**
 * Testes de integração para o fluxo de autenticação (Auth).
 * Valida o ciclo de vida do usuário: registro, login, renovação de tokens e
 * logout.
 */
@DisplayName("Integração: AuthService")
class AuthServiceIT extends IntegrationTestBase {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private RefreshTokenRepository refreshTokenRepository;

        // -------------------------------------------------------------------------
        // Cenários de sucesso
        // -------------------------------------------------------------------------

        @Nested
        @DisplayName("Dado um fluxo de autenticação válido")
        class Success {

                /**
                 * Valida a persistência do usuário e se o hash da senha foi aplicado
                 * corretamente.
                 */
                @Test
                @DisplayName("deve registrar novo usuário com senha criptografada")
                void shouldRegisterUserWithHashedPassword() throws Exception {
                        var request = new RegisterUserRequestDTO("gabriel@email.com", "senha123", "Gabriel");

                        mockMvc.perform(post("/auth/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isCreated())
                                        .andExpect(jsonPath("$.accessToken").exists())
                                        .andExpect(jsonPath("$.refreshToken").exists());

                        var user = userRepository.findByEmail("gabriel@email.com").orElseThrow();

                        assertTrue(passwordEncoder.matches("senha123", user.getPassword()));
                        assertNotEquals("senha123", user.getPassword());
                }

                /**
                 * Verifica a emissão de tokens JWT para credenciais válidas.
                 */
                @Test
                @DisplayName("deve realizar login e retornar tokens")
                void shouldLoginWhenCredentialsAreValid() throws Exception {
                        userRepository.save(User.createStandard("Test", "test@email.com",
                                        passwordEncoder.encode("pass123")));

                        var loginRequest = new LoginRequestDTO("test@email.com", "pass123");

                        mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(loginRequest)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.accessToken").isString())
                                        .andExpect(jsonPath("$.refreshToken").isString());
                }

                /**
                 * Valida o mecanismo de refresh para estender a sessão do usuário.
                 */
                @Test
                @DisplayName("deve gerar novo access token usando refresh token válido")
                void shouldRefreshAccessToken() throws Exception {
                        var registerRequest = new RegisterUserRequestDTO("test@email.com", "pass1234", "Test");
                        var result = mockMvc.perform(post("/auth/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(registerRequest)))
                                        .andReturn();

                        var body = objectMapper.readTree(result.getResponse().getContentAsString());
                        String refreshToken = body.get("refreshToken").asText();
                        String accessToken = body.get("accessToken").asText();

                        mockMvc.perform(post("/auth/refresh")
                                        .header("Authorization", "Bearer " + accessToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(Map.of("token", refreshToken))))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.accessToken").isString());
                }

                /**
                 * Garante que o logout invalida a sessão removendo o token de atualização.
                 */
                @Test
                @DisplayName("deve remover refresh token do banco ao realizar logout")
                void shouldRemoveRefreshTokenOnLogout() throws Exception {
                        userRepository.save(User.createStandard("Test", "logout@email.com",
                                        passwordEncoder.encode("pass1234")));

                        var loginResult = mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new LoginRequestDTO("logout@email.com", "pass1234"))))
                                        .andReturn();

                        var body = objectMapper.readTree(loginResult.getResponse().getContentAsString());
                        String accessToken = body.get("accessToken").asText();
                        String refreshToken = body.get("refreshToken").asText();

                        mockMvc.perform(post("/auth/logout")
                                        .header("Authorization", "Bearer " + accessToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(Map.of("token", refreshToken))))
                                        .andExpect(status().isNoContent());

                        assertEquals(0, refreshTokenRepository.count());
                }
        }

        // -------------------------------------------------------------------------
        // Cenários de erro / falha
        // -------------------------------------------------------------------------

        @Nested
        @DisplayName("Dado um fluxo de autenticação inválido")
        class Failures {

                @Test
                @DisplayName("deve retornar 409 ao registrar email já existente")
                void shouldReturnConflictWhenEmailExists() throws Exception {
                        userRepository.save(User.createStandard("Original", "duplicado@email.com",
                                        passwordEncoder.encode("senha123")));
                        var request = new RegisterUserRequestDTO("duplicado@email.com", "senha123", "Original");

                        mockMvc.perform(post("/auth/signup")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                        .andExpect(status().isConflict());
                }

                @Test
                @DisplayName("deve retornar 401 para senha incorreta ou email inexistente")
                void shouldReturnUnauthorizedForWrongCredentials() throws Exception {
                        userRepository.save(User.createStandard("Test", "test@email.com",
                                        passwordEncoder.encode("pass123")));

                        // Senha errada
                        mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new LoginRequestDTO("test@email.com", "wrong"))))
                                        .andExpect(status().isUnauthorized());

                        // Email inexistente
                        mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new LoginRequestDTO("nao@existe.com", "pass123"))))
                                        .andExpect(status().isUnauthorized());
                }

                @Test
                @DisplayName("deve retornar 401 ao tentar refresh com token inválido")
                void shouldReturnUnauthorizedForInvalidRefresh() throws Exception {
                        userRepository.save(User.createStandard("Test", "test@email.com",
                                        passwordEncoder.encode("pass123")));
                        var result = mockMvc.perform(post("/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(
                                                        new LoginRequestDTO("test@email.com", "pass123"))))
                                        .andReturn();

                        String accessToken = objectMapper.readTree(result.getResponse().getContentAsString())
                                        .get("accessToken").asText();

                        mockMvc.perform(post("/auth/refresh")
                                        .header("Authorization", "Bearer " + accessToken)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(Map.of("token", "token-invalido"))))
                                        .andExpect(status().isUnauthorized());
                }
        }
}