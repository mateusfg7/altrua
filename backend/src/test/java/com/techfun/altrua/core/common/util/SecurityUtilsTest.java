package com.techfun.altrua.core.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.infra.security.userdetails.UserPrincipal;

/**
 * Testes unitários para a utilitária de segurança.
 * Valida a extração de informações do SecurityContextHolder e o tratamento de
 * estados de autenticação inválidos.
 */
@DisplayName("Utilitário: SecurityUtils")
class SecurityUtilsTest {

    // -------------------------------------------------------------------------
    // Cenários de Sucesso
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao recuperar dados do usuário autenticado")
    class Success {

        /**
         * Valida a extração correta do UUID quando o contexto está íntegro.
         */
        @Test
        @DisplayName("deve retornar o UUID do usuário quando o principal é válido")
        void shouldReturnUserIdWhenAllDataIsValid() {
            UUID expectedId = UUID.randomUUID();

            try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
                User user = BeanUtils.instantiateClass(User.class);
                ReflectionTestUtils.setField(user, "id", expectedId);
                UserPrincipal principal = new UserPrincipal(user);

                Authentication auth = mock(Authentication.class);
                SecurityContext context = mock(SecurityContext.class);

                mockedContext.when(SecurityContextHolder::getContext).thenReturn(context);
                when(context.getAuthentication()).thenReturn(auth);
                when(auth.isAuthenticated()).thenReturn(true);
                when(auth.getPrincipal()).thenReturn(principal);

                UUID result = SecurityUtils.getCurrentUserId();

                assertEquals(expectedId, result);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Cenários de Falha
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao lidar com estados de autenticação inválidos")
    class Failures {

        /**
         * Garante que o sistema não tente processar um Principal que não seja do tipo
         * esperado.
         */
        @Test
        @DisplayName("deve lançar AuthenticationServiceException quando o principal não for UserPrincipal")
        void shouldThrowExceptionWhenPrincipalIsInvalidType() {
            try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
                SecurityContext context = mock(SecurityContext.class);
                Authentication auth = mock(Authentication.class);

                mockedContext.when(SecurityContextHolder::getContext).thenReturn(context);
                when(context.getAuthentication()).thenReturn(auth);
                when(auth.isAuthenticated()).thenReturn(true);
                when(auth.getPrincipal()).thenReturn("usuario_estranho");

                AuthenticationServiceException ex = assertThrows(AuthenticationServiceException.class,
                        SecurityUtils::getCurrentUserId);

                assertEquals("O objeto Principal no SecurityContext não é do tipo UserPrincipal", ex.getMessage());
            }
        }

        /**
         * Verifica o bloqueio de acesso para tokens anônimos.
         */
        @Test
        @DisplayName("deve lançar InsufficientAuthenticationException para token anônimo")
        void shouldThrowExceptionWhenTokenIsAnonymous() {
            try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
                SecurityContext context = mock(SecurityContext.class);
                AnonymousAuthenticationToken anonymousToken = mock(AnonymousAuthenticationToken.class);

                mockedContext.when(SecurityContextHolder::getContext).thenReturn(context);
                when(context.getAuthentication()).thenReturn(anonymousToken);
                when(anonymousToken.isAuthenticated()).thenReturn(true);

                assertThrows(InsufficientAuthenticationException.class, SecurityUtils::getCurrentUserId);
            }
        }

        /**
         * Valida o comportamento para contextos vazios (sem objeto Authentication).
         */
        @Test
        @DisplayName("deve lançar InsufficientAuthenticationException quando o Authentication for nulo")
        void shouldThrowExceptionWhenAuthenticationIsNull() {
            try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
                SecurityContext context = mock(SecurityContext.class);
                mockedContext.when(SecurityContextHolder::getContext).thenReturn(context);
                when(context.getAuthentication()).thenReturn(null);

                assertThrows(InsufficientAuthenticationException.class, SecurityUtils::getCurrentUserId);
            }
        }

        /**
         * Verifica o erro quando o objeto existe, mas o status de autenticação é falso.
         */
        @Test
        @DisplayName("deve lançar InsufficientAuthenticationException quando não estiver autenticado")
        void shouldThrowExceptionWhenIsNotAuthenticated() {
            try (MockedStatic<SecurityContextHolder> mockedContext = mockStatic(SecurityContextHolder.class)) {
                SecurityContext context = mock(SecurityContext.class);
                Authentication auth = mock(Authentication.class);

                mockedContext.when(SecurityContextHolder::getContext).thenReturn(context);
                when(context.getAuthentication()).thenReturn(auth);
                when(auth.isAuthenticated()).thenReturn(false);

                assertThrows(InsufficientAuthenticationException.class, SecurityUtils::getCurrentUserId);
            }
        }
    }
}