package com.techfun.altrua.unit.features.event.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;

/**
 * Testes de unidade para o domínio de Eventos e gestão de ações sociais.
 * Garante a integridade das transições de status, regras de capacidade de
 * voluntários e consistência dos ganchos de auditoria temporal.
 */
@DisplayName("Domínio: Evento")
class EventTest {

    private Event event;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .title("Ação Social Teste")
                .status(EventStatusEnum.PUBLISHED)
                .acceptsVolunteers(true)
                .maxVolunteers(10)
                .startsAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Nested
    @DisplayName("Ao finalizar um evento")
    class FinishEvent {

        /**
         * Garante que o fluxo principal de encerramento atualiza o status e a data
         * final.
         */
        @Test
        @DisplayName("deve alterar status para FINISHED e definir data de encerramento")
        void shouldFinishSuccess() {
            event.finish();

            assertThat(event.getStatus()).isEqualTo(EventStatusEnum.FINISHED);
            assertThat(event.getEndsAt()).isBeforeOrEqualTo(Instant.now());
        }

        /**
         * Impede inconsistência de encerrar algo que já foi cancelado.
         */
        @Test
        @DisplayName("deve lançar exceção ao tentar finalizar evento cancelado")
        void shouldThrowExceptionWhenCancelled() {
            event.setStatus(EventStatusEnum.CANCELLED);

            assertThatThrownBy(() -> event.finish())
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("cancelado");
        }
    }

    @Nested
    @DisplayName("Ao validar novos voluntários")
    class VolunteerValidation {

        /**
         * Valida a regra de limite de vagas.
         */
        @Test
        @DisplayName("deve recusar novos voluntários quando atingir o limite máximo")
        void shouldRefuseWhenLimitReached() {
            boolean accepts = event.acceptsNewVolunteers(10);
            assertThat(accepts).isFalse();
        }

        /**
         * Valida se a flag de interrupção de inscrições é respeitada.
         */
        @Test
        @DisplayName("deve recusar quando a flag de aceitar voluntários estiver desativada")
        void shouldRefuseWhenFlagIsFalse() {
            event.setAcceptsVolunteers(false);
            boolean accepts = event.acceptsNewVolunteers(5);
            assertThat(accepts).isFalse();
        }

        @Test
        @DisplayName("deve aceitar quando houver vagas e flag estiver ativa")
        void shouldAcceptWhenHasSlots() {
            boolean accepts = event.acceptsNewVolunteers(9);
            assertThat(accepts).isTrue();
        }
    }
}