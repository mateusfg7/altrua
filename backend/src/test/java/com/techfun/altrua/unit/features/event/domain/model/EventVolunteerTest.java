package com.techfun.altrua.unit.features.event.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.EventVolunteer;
import com.techfun.altrua.features.user.domain.model.User;

/**
 * Testes de unidade para o vínculo entre voluntários e eventos.
 * Valida a máquina de estados das inscrições, assegurando o registro correto
 * de cancelamentos, reativações e a persistência dos marcos de auditoria.
 */
@DisplayName("Domínio: Vínculo de Voluntário")
class EventVolunteerTest {

    private Event event;
    private User user;
    private EventVolunteer enrollment;

    @BeforeEach
    void setUp() {
        event = mock(Event.class);
        user = mock(User.class);
        enrollment = EventVolunteer.enroll(event, user);
    }

    @Nested
    @DisplayName("Ao gerenciar inscrições")
    class EnrollmentLifecycle {

        /**
         * Valida o estado inicial do vínculo, assegurando que novas inscrições
         * nasçam ativas e corretamente associadas ao evento e ao voluntário.
         */
        @Test
        @DisplayName("deve iniciar inscrição com status CONFIRMED")
        void shouldStartAsConfirmed() {
            assertThat(enrollment.getStatus()).isEqualTo(VolunteerStatusEnum.CONFIRMED);
            assertThat(enrollment.getEvent()).isEqualTo(event);
            assertThat(enrollment.getUser()).isEqualTo(user);
        }

        /**
         * Verifica a transição para o estado de cancelamento e a obrigatoriedade
         * do registro do carimbo de data/hora da operação.
         */
        @Test
        @DisplayName("deve cancelar inscrição e registrar data de cancelamento")
        void shouldCancelEnrollment() {
            enrollment.cancel();

            assertThat(enrollment.getStatus()).isEqualTo(VolunteerStatusEnum.CANCELLED);
            assertThat(enrollment.getCancelledAt()).isNotNull();
        }

        /**
         * Testa o fluxo de reversão do cancelamento, garantindo o retorno ao status
         * confirmado e a consequente limpeza do marco temporal de desistência.
         */
        @Test
        @DisplayName("deve reativar inscrição e limpar data de cancelamento")
        void shouldReactivateEnrollment() {
            enrollment.cancel();
            enrollment.reactivate();

            assertThat(enrollment.getStatus()).isEqualTo(VolunteerStatusEnum.CONFIRMED);
            assertThat(enrollment.getCancelledAt()).isNull();
        }
    }
}