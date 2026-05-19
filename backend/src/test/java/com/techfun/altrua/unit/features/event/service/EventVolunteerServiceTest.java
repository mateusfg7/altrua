package com.techfun.altrua.unit.features.event.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import org.springframework.dao.DataIntegrityViolationException;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.EventVolunteer;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.event.repository.EventVolunteerRepository;
import com.techfun.altrua.features.event.service.EventVolunteerService;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.repository.UserRepository;

/**
 * Testes de unidade para o gerenciamento de inscrições de voluntários
 * (EventVolunteerService).
 * Valida o cumprimento das regras de negócio de capacidade de eventos,
 * idempotência de ações e tratamento de exceções de integridade sob
 * concorrência simulada.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Serviço: EventVolunteerService")
class EventVolunteerServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventVolunteerRepository eventVolunteerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventVolunteerService eventVolunteerService;

    private MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    private final UUID eventId = UUID.randomUUID();
    private final UUID ongId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
        securityUtilsMockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMockedStatic.close();
    }

    // =========================================================================
    // Fluxos de Inscrição (Enroll)
    // =========================================================================

    @Nested
    @DisplayName("Ao realizar inscrição (Enroll)")
    class Enroll {

        /**
         * Garante a criação de uma nova inscrição ativa quando o evento existe,
         * possui vagas e o usuário ainda não possui vínculo prévio.
         */
        @Test
        @DisplayName("deve salvar nova inscrição quando dados forem válidos e houver vagas")
        void shouldCreateNewEnrollmentWhenPayloadIsValidAndHasVacancies() {
            Event event = mock(Event.class);
            User user = mock(User.class);

            when(eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)).thenReturn(Optional.of(event));
            when(eventVolunteerRepository.findByEventIdAndUserId(eventId, userId)).thenReturn(Optional.empty());
            when(eventVolunteerRepository.countByEventIdAndStatus(eventId, VolunteerStatusEnum.CONFIRMED))
                    .thenReturn(5L);
            when(event.acceptsNewVolunteers(5L)).thenReturn(true);
            when(userRepository.getReferenceById(userId)).thenReturn(user);

            eventVolunteerService.enroll(eventId, ongId);

            verify(eventVolunteerRepository).saveAndFlush(any(EventVolunteer.class));
        }

        /**
         * Impede o processamento se o evento não for localizado através do par
         * identificador
         * do evento e da ONG proprietária.
         */
        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando o evento não existir")
        void shouldThrowNotFoundExceptionWhenEventDoesNotExist() {
            when(eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventVolunteerService.enroll(eventId, ongId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(eventVolunteerRepository, never()).saveAndFlush(any());
        }

        /**
         * Regra de negócio: impede duplicidade de inscrições se o usuário já possuir
         * um registro ativo no evento informado.
         */
        @Test
        @DisplayName("deve lançar DomainException quando o usuário já estiver inscrito")
        void shouldThrowDomainExceptionWhenUserIsAlreadyEnrolled() {
            Event event = mock(Event.class);
            EventVolunteer existingVolunteer = mock(EventVolunteer.class);

            when(eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)).thenReturn(Optional.of(event));
            when(eventVolunteerRepository.findByEventIdAndUserId(eventId, userId))
                    .thenReturn(Optional.of(existingVolunteer));
            when(existingVolunteer.getStatus()).thenReturn(VolunteerStatusEnum.CONFIRMED);

            assertThatThrownBy(() -> eventVolunteerService.enroll(eventId, ongId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Você já está inscrito nesse evento");
        }

        /**
         * Regra de negócio: barra novas inscrições se o cálculo de capacidade do evento
         * indicar que o limite máximo de voluntários confirmados foi atingido.
         */
        @Test
        @DisplayName("deve lançar DomainException quando o evento estiver lotado")
        void shouldThrowDomainExceptionWhenEventIsFull() {
            Event event = mock(Event.class);

            when(eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)).thenReturn(Optional.of(event));
            when(eventVolunteerRepository.findByEventIdAndUserId(eventId, userId)).thenReturn(Optional.empty());
            when(eventVolunteerRepository.countByEventIdAndStatus(eventId, VolunteerStatusEnum.CONFIRMED))
                    .thenReturn(10L);
            when(event.acceptsNewVolunteers(10L)).thenReturn(false);

            assertThatThrownBy(() -> eventVolunteerService.enroll(eventId, ongId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("O evento não aceita mais voluntários.");
        }

        /**
         * Valida o fluxo de reativação: se o histórico apontar uma inscrição cancelada,
         * o sistema deve apenas mudar o estado para ativo em vez de gerar um novo
         * registro.
         */
        @Test
        @DisplayName("deve reativar inscrição existente se o status anterior for CANCELLED")
        void shouldReactivateEnrollmentWhenPreviousStatusIsCancelled() {
            Event event = mock(Event.class);
            EventVolunteer existingVolunteer = mock(EventVolunteer.class);

            when(eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)).thenReturn(Optional.of(event));
            when(eventVolunteerRepository.findByEventIdAndUserId(eventId, userId))
                    .thenReturn(Optional.of(existingVolunteer));
            when(existingVolunteer.getStatus()).thenReturn(VolunteerStatusEnum.CANCELLED);
            when(eventVolunteerRepository.countByEventIdAndStatus(eventId, VolunteerStatusEnum.CONFIRMED))
                    .thenReturn(2L);
            when(event.acceptsNewVolunteers(2L)).thenReturn(true);

            eventVolunteerService.enroll(eventId, ongId);

            verify(existingVolunteer).reactivate();
            verify(eventVolunteerRepository).save(existingVolunteer);
            verify(eventVolunteerRepository, never()).saveAndFlush(any());
        }

        /**
         * Proteção concorrente: simula o cenário onde duas requisições idênticas passam
         * pela validação em memória ao mesmo tempo, garantindo que o erro de constraint
         * do banco seja tratado.
         */
        @Test
        @DisplayName("deve tratar DataIntegrityViolationException e lançar DomainException amigável")
        void shouldHandleDataIntegrityExceptionAndThrowDomainException() {
            Event event = mock(Event.class);
            User user = mock(User.class);

            when(eventRepository.findByIdAndOngIdForUpdate(eventId, ongId)).thenReturn(Optional.of(event));
            when(eventVolunteerRepository.findByEventIdAndUserId(eventId, userId)).thenReturn(Optional.empty());
            when(eventVolunteerRepository.countByEventIdAndStatus(eventId, VolunteerStatusEnum.CONFIRMED))
                    .thenReturn(0L);
            when(event.acceptsNewVolunteers(0L)).thenReturn(true);
            when(userRepository.getReferenceById(userId)).thenReturn(user);
            when(eventVolunteerRepository.saveAndFlush(any())).thenThrow(DataIntegrityViolationException.class);

            assertThatThrownBy(() -> eventVolunteerService.enroll(eventId, ongId))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Você já está inscrito nesse evento");
        }
    }

    // =========================================================================
    // Fluxos de Cancelamento (CancelEnrollment)
    // =========================================================================

    @Nested
    @DisplayName("Ao cancelar inscrição (CancelEnrollment)")
    class CancelEnrollment {

        /**
         * Verifica o fluxo feliz de cancelamento, alterando o status interno
         * da entidade recuperada de forma segura com lock.
         */
        @Test
        @DisplayName("deve cancelar a inscrição com sucesso se estiver CONFIRMED")
        void shouldCancelEnrollmentSuccessfully() {
            EventVolunteer volunteer = mock(EventVolunteer.class);

            when(eventVolunteerRepository.findByEventIdAndOngIdAndUserIdForUpdate(eventId, ongId, userId))
                    .thenReturn(Optional.of(volunteer));
            when(volunteer.getStatus()).thenReturn(VolunteerStatusEnum.CONFIRMED);

            eventVolunteerService.cancelEnrollment(eventId, ongId);

            verify(volunteer).cancel();
            verify(eventVolunteerRepository).save(volunteer);
        }

        /**
         * Garante a idempotência da API: se a inscrição já constar como cancelada,
         * o serviço deve retornar imediatamente sem reprocessar ou salvar novamente.
         */
        @Test
        @DisplayName("deve retornar silenciosamente (idempotência) se a inscrição já estiver CANCELLED")
        void shouldReturnSilentlyWhenEnrollmentIsAlreadyCancelled() {
            EventVolunteer volunteer = mock(EventVolunteer.class);

            when(eventVolunteerRepository.findByEventIdAndOngIdAndUserIdForUpdate(eventId, ongId, userId))
                    .thenReturn(Optional.of(volunteer));
            when(volunteer.getStatus()).thenReturn(VolunteerStatusEnum.CANCELLED);

            eventVolunteerService.cancelEnrollment(eventId, ongId);

            verify(volunteer, never()).cancel();
            verify(eventVolunteerRepository, never()).save(any());
        }

        /**
         * Protege o fluxo de cancelamento contra identificadores inexistentes ou
         * desalinhados
         * entre o usuário e o contexto da ONG.
         */
        @Test
        @DisplayName("deve lançar ResourceNotFoundException se a inscrição não for localizada")
        void shouldThrowNotFoundExceptionWhenEnrollmentNotFound() {
            when(eventVolunteerRepository.findByEventIdAndOngIdAndUserIdForUpdate(eventId, ongId, userId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventVolunteerService.cancelEnrollment(eventId, ongId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}