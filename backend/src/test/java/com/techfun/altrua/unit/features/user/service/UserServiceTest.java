package com.techfun.altrua.unit.features.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.techfun.altrua.core.common.exceptions.InvalidCredentialsException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.user.api.UserMapper;
import com.techfun.altrua.features.user.api.dto.ChangeEmailRequestDTO;
import com.techfun.altrua.features.user.api.dto.ChangePasswordRequestDTO;
import com.techfun.altrua.features.user.api.dto.UpdateUserRequestDTO;
import com.techfun.altrua.features.user.api.dto.UserResponseDTO;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.repository.UserRepository;
import com.techfun.altrua.features.user.service.UserService;

/**
 * Testes de unidade para o gerenciamento do perfil do usuário autenticado
 * (UserService).
 * Valida o cumprimento das regras de negócio de atualização de perfil,
 * troca de senha e e-mail, e tratamento correto de exceções.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Serviço: UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    private final UUID userId = UUID.randomUUID();
    private User user;

    @BeforeEach
    void setUp() {
        user = User.createStandard("Gabriel Henrique", "gabriel@altrua.org", "senhaCriptografada");
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        securityUtilsMockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMockedStatic.close();
    }

    // =========================================================================
    // Fluxos de Atualização de Perfil (Update)
    // =========================================================================

    @Nested
    @DisplayName("Ao atualizar o perfil (update)")
    class Update {

        /**
         * Garante que nome e avatar são atualizados corretamente quando
         * ambos os campos são informados no DTO.
         */
        @Test
        @DisplayName("deve atualizar nome e avatar quando ambos são informados")
        void shouldUpdateNameAndAvatarWhenBothFieldsAreProvided() {
            UpdateUserRequestDTO dto = new UpdateUserRequestDTO("Novo Nome", "https://exemplo.com/avatar.png");
            UserResponseDTO responseDTO = new UserResponseDTO(userId, "Novo Nome", user.getEmail(),
                    "https://exemplo.com/avatar.png", null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toDTO(user)).thenReturn(responseDTO);

            UserResponseDTO result = userService.update(dto);

            assertThat(result.name()).isEqualTo("Novo Nome");
            assertThat(result.avatarUrl()).isEqualTo("https://exemplo.com/avatar.png");
        }

        /**
         * Garante que campos nulos no DTO são ignorados, preservando
         * os valores anteriores da entidade.
         */
        @Test
        @DisplayName("deve ignorar campo nulo e preservar valor anterior")
        void shouldIgnoreNullFieldsAndPreservePreviousValues() {
            UpdateUserRequestDTO dto = new UpdateUserRequestDTO("Novo Nome", null);
            UserResponseDTO responseDTO = new UserResponseDTO(userId, "Novo Nome", user.getEmail(), null, null, null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toDTO(user)).thenReturn(responseDTO);

            UserResponseDTO result = userService.update(dto);

            assertThat(result.avatarUrl()).isNull();
        }
    }

    // =========================================================================
    // Fluxos de Troca de Senha (ChangePassword)
    // =========================================================================

    @Nested
    @DisplayName("Ao alterar a senha (changePassword)")
    class ChangePassword {

        /**
         * Garante que a senha é atualizada corretamente quando
         * a senha atual informada corresponde à cadastrada.
         */
        @Test
        @DisplayName("deve alterar a senha quando a senha atual está correta")
        void shouldUpdatePasswordWhenCurrentPasswordMatches() {
            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("senha123", "novaSenha123");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(true);
            when(passwordEncoder.encode(dto.newPassword())).thenReturn("novaSenhaCriptografada");

            userService.changePassword(dto);

            assertThat(user.getPassword()).isEqualTo("novaSenhaCriptografada");
        }

        /**
         * Regra de negócio: impede a troca de senha se a senha atual
         * informada não corresponder à senha cadastrada.
         */
        @Test
        @DisplayName("deve lançar InvalidCredentialsException quando a senha atual está incorreta")
        void shouldThrowInvalidCredentialsWhenCurrentPasswordDoesNotMatch() {
            ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO("senhaErrada", "novaSenha123");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.changePassword(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }

    // =========================================================================
    // Fluxos de Troca de E-mail (ChangeEmail)
    // =========================================================================

    @Nested
    @DisplayName("Ao alterar o e-mail (changeEmail)")
    class ChangeEmail {

        /**
         * Garante que o e-mail é atualizado corretamente quando
         * a senha atual informada corresponde à cadastrada.
         */
        @Test
        @DisplayName("deve alterar o e-mail quando a senha atual está correta")
        void shouldUpdateEmailWhenCurrentPasswordMatches() {
            ChangeEmailRequestDTO dto = new ChangeEmailRequestDTO("senha123", "novo@altrua.org");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(true);

            userService.changeEmail(dto);

            assertThat(user.getEmail()).isEqualTo("novo@altrua.org");
        }

        /**
         * Regra de negócio: impede a troca de e-mail se a senha atual
         * informada não corresponder à senha cadastrada.
         */
        @Test
        @DisplayName("deve lançar InvalidCredentialsException quando a senha atual está incorreta")
        void shouldThrowInvalidCredentialsWhenCurrentPasswordDoesNotMatch() {
            ChangeEmailRequestDTO dto = new ChangeEmailRequestDTO("senhaErrada", "novo@altrua.org");

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.currentPassword(), user.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> userService.changeEmail(dto))
                    .isInstanceOf(InvalidCredentialsException.class);
        }
    }

    // =========================================================================
    // Fluxos de Obtenção do Perfil (GetMe)
    // =========================================================================

    @Nested
    @DisplayName("Ao obter o perfil autenticado (getMe)")
    class GetMe {

        /**
         * Garante que o DTO do usuário autenticado é retornado
         * corretamente quando o registro existe na base.
         */
        @Test
        @DisplayName("deve retornar o DTO do usuário autenticado")
        void shouldReturnCurrentUserDTO() {
            UserResponseDTO responseDTO = new UserResponseDTO(userId, user.getName(), user.getEmail(), null, null,
                    null);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toDTO(user)).thenReturn(responseDTO);

            UserResponseDTO result = userService.getMe();

            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.email()).isEqualTo(user.getEmail());
        }
    }
}