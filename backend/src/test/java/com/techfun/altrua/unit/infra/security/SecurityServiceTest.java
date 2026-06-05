package com.techfun.altrua.unit.infra.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.domain.model.OngAdministrator;
import com.techfun.altrua.features.ong.domain.model.OngAdministratorId;
import com.techfun.altrua.features.ong.repository.OngAdministratorRepository;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.infra.security.SecurityService;

@DisplayName("Serviço de Segurança: SecurityService")
@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private OngAdministratorRepository ongAdministratorRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private SecurityService securityService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    private final UUID userId = UUID.randomUUID();
    private final UUID ongId = UUID.randomUUID();
    private final UUID eventId = UUID.randomUUID();

    @BeforeEach
    void mockAuthenticatedUser() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
    }

    @AfterEach
    void closeMocks() {
        securityUtilsMock.close();
    }

    // -------------------------------------------------------------------------
    // Validação de Administrador de ONG
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao verificar se o usuário é administrador da ONG")
    class IsOngAdmin {

        /**
         * O vínculo administrativo existe no repositório — acesso deve ser concedido.
         */
        @Test
        @DisplayName("deve retornar true quando o vínculo administrativo existe")
        void shouldReturnTrueWhenAdminBindingExists() {
            when(ongAdministratorRepository.existsById(new OngAdministratorId(userId, ongId)))
                    .thenReturn(true);

            assertTrue(securityService.isOngAdmin(ongId));
        }

        /**
         * Nenhum vínculo encontrado — acesso deve ser negado.
         */
        @Test
        @DisplayName("deve retornar false quando o vínculo administrativo não existe")
        void shouldReturnFalseWhenAdminBindingDoesNotExist() {
            when(ongAdministratorRepository.existsById(new OngAdministratorId(userId, ongId)))
                    .thenReturn(false);

            assertFalse(securityService.isOngAdmin(ongId));
        }
    }

    // -------------------------------------------------------------------------
    // Validação de Criador de ONG
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao verificar se o usuário é criador da ONG")
    class IsOngCreator {

        /**
         * Repositório confirma que o usuário é o criador — privilégio deve ser
         * reconhecido.
         */
        @Test
        @DisplayName("deve retornar true quando o usuário é o criador da ONG")
        void shouldReturnTrueWhenUserIsCreator() {
            when(ongAdministratorRepository.checkIsCreator(userId, ongId))
                    .thenReturn(Optional.of(true));

            assertTrue(securityService.isOngCreator(ongId));
        }

        /**
         * Repositório indica explicitamente que o usuário não é o criador.
         */
        @Test
        @DisplayName("deve retornar false quando o usuário não é o criador da ONG")
        void shouldReturnFalseWhenUserIsNotCreator() {
            when(ongAdministratorRepository.checkIsCreator(userId, ongId))
                    .thenReturn(Optional.of(false));

            assertFalse(securityService.isOngCreator(ongId));
        }

        /**
         * Repositório não retorna valor — o fallback {@code orElse(false)} deve
         * garantir acesso negado.
         */
        @Test
        @DisplayName("deve retornar false quando o repositório retorna Optional vazio")
        void shouldReturnFalseWhenRepositoryReturnsEmptyOptional() {
            when(ongAdministratorRepository.checkIsCreator(userId, ongId))
                    .thenReturn(Optional.empty());

            assertFalse(securityService.isOngCreator(ongId));
        }
    }

    // -------------------------------------------------------------------------
    // Autorização de Gerenciamento de Evento
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao verificar se o usuário pode gerenciar um evento")
    class CanManageEvent {

        /**
         * Evento existe, pertence à ONG e o usuário está na lista de
         * administradores — acesso deve ser concedido.
         */
        @Test
        @DisplayName("deve retornar true quando o usuário é admin da ONG proprietária do evento")
        void shouldReturnTrueWhenUserIsOngAdmin() {
            when(eventRepository.findByIdWithOngAndAdmins(eventId))
                    .thenReturn(Optional.of(buildEvent(ongId, userId)));

            assertTrue(securityService.canManageEvent(ongId, eventId));
        }

        /**
         * Evento existe e pertence à ONG, porém o usuário autenticado não consta
         * na lista de administradores — acesso deve ser negado.
         */
        @Test
        @DisplayName("deve retornar false quando o usuário não é admin da ONG proprietária do evento")
        void shouldReturnFalseWhenUserIsNotOngAdmin() {
            when(eventRepository.findByIdWithOngAndAdmins(eventId))
                    .thenReturn(Optional.of(buildEvent(ongId, UUID.randomUUID())));

            assertFalse(securityService.canManageEvent(ongId, eventId));
        }

        /**
         * Nenhum evento encontrado com o ID informado — deve lançar exceção
         * para evitar vazamento de informação.
         */
        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o evento não existe")
        void shouldThrowResourceNotFoundExceptionWhenEventDoesNotExist() {
            when(eventRepository.findByIdWithOngAndAdmins(eventId))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> securityService.canManageEvent(ongId, eventId));
        }

        /**
         * O evento existe mas está vinculado a uma ONG diferente da informada na
         * rota — deve lançar exceção para impedir enumeração de recursos.
         */
        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o evento pertence a outra ONG")
        void shouldThrowResourceNotFoundExceptionWhenEventBelongsToAnotherOng() {
            when(eventRepository.findByIdWithOngAndAdmins(eventId))
                    .thenReturn(Optional.of(buildEvent(UUID.randomUUID(), userId)));

            assertThrows(ResourceNotFoundException.class,
                    () -> securityService.canManageEvent(ongId, eventId));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Monta um {@link Event} com uma ONG contendo um único administrador cujo
     * {@link User} possui o {@code adminUserId} informado.
     *
     * @param eventOngId  ID que será atribuído à ONG do evento.
     * @param adminUserId ID do usuário vinculado como administrador.
     * @return instância de {@link Event} pronta para uso nos testes.
     */
    private Event buildEvent(UUID eventOngId, UUID adminUserId) {
        User user = User.createStandard("Gabriel Henrique", "gabriel@altrua.org", "senhaCriptografada");
        ReflectionTestUtils.setField(user, "id", adminUserId);

        Ong ong = Ong.builder()
                .name("ONG Teste")
                .build();
        ReflectionTestUtils.setField(ong, "id", eventOngId);

        OngAdministrator.createCreator(user, ong);

        Event event = Event.builder()
                .title("Evento Teste")
                .ong(ong)
                .build();

        return event;
    }
}