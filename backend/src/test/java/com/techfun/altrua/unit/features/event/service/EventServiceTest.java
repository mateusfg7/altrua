package com.techfun.altrua.unit.features.event.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.hibernate.exception.ConstraintViolationException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.core.common.exceptions.DuplicateResourceException;
import com.techfun.altrua.core.common.exceptions.ResourceNotFoundException;
import com.techfun.altrua.core.common.util.SecurityUtils;
import com.techfun.altrua.core.common.util.SlugUtils;
import com.techfun.altrua.features.event.api.EventMapper;
import com.techfun.altrua.features.event.api.dto.EventFilterDTO;
import com.techfun.altrua.features.event.api.dto.EventListResponseDTO;
import com.techfun.altrua.features.event.api.dto.EventResponseDTO;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.api.dto.UpdateEventRequestDTO;
import com.techfun.altrua.features.event.domain.enums.VolunteerStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.repository.EventRepository;
import com.techfun.altrua.features.event.repository.EventVolunteerRepository;
import com.techfun.altrua.features.event.service.EventService;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.ong.repository.OngRepository;
import com.techfun.altrua.features.tag.service.TagService;
import com.techfun.altrua.features.user.domain.model.User;
import com.techfun.altrua.features.user.repository.UserRepository;

/**
 * Testes unitários para {@link EventService}.
 *
 * <p>
 * Cobre as regras de negócio de criação, atualização, encerramento e
 * listagem de eventos. Todas as dependências são mockadas via Mockito,
 * incluindo utilitários estáticos ({@link SecurityUtils} e {@link SlugUtils})
 * interceptados com {@link MockedStatic}.
 * </p>
 *
 * <p>
 * Os testes estão organizados em classes aninhadas por método do serviço,
 * facilitando a leitura e a navegação.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    EventVolunteerRepository eventVolunteerRepository;
    @Mock
    OngRepository ongRepository;
    @Mock
    EventMapper eventMapper;
    @Mock
    TagService tagService;

    @InjectMocks
    EventService eventService;

    private UUID ongId;
    private UUID userId;
    private UUID eventId;
    private User creator;
    private Ong ong;
    private Event event;

    /** Inicializa os UUIDs e mocks de entidade comuns a todos os testes. */
    @BeforeEach
    void setUp() {
        ongId = UUID.randomUUID();
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        creator = mock(User.class);
        ong = mock(Ong.class);
        event = mock(Event.class);
    }

    // -------------------------------------------------------------------------
    // register
    // -------------------------------------------------------------------------

    /**
     * Testes para {@link EventService#register(UUID, RegisterEventRequestDTO)}.
     *
     * <p>
     * Verifica geração de slug, tratamento de colisões de slug via banco
     * e rejeição de intervalos de datas inválidos.
     * </p>
     */
    @Nested
    @DisplayName("register()")
    class Register {

        /**
         * Monta um {@link RegisterEventRequestDTO} com apenas título, tags e datas
         * preenchidos; demais campos permanecem nulos.
         */
        private RegisterEventRequestDTO buildRequest(Instant startsAt, Instant endsAt) {
            return new RegisterEventRequestDTO(
                    "Evento de Teste",
                    "Descrição",
                    null,
                    null,
                    null,
                    null,
                    true,
                    null,
                    null,
                    null,
                    null,
                    startsAt,
                    endsAt,
                    Set.of("tag1"));
        }

        /** Fluxo feliz: slug disponível no banco, evento persistido e DTO retornado. */
        @Test
        @DisplayName("deve registrar evento com slug único")
        void shouldRegisterEventWithUniqueSlug() {
            Instant starts = Instant.now().plus(1, ChronoUnit.DAYS);
            RegisterEventRequestDTO request = buildRequest(starts, starts.plus(2, ChronoUnit.HOURS));

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class);
                    MockedStatic<SlugUtils> slugUtils = mockStatic(SlugUtils.class)) {

                secUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                slugUtils.when(() -> SlugUtils.normalize(any())).thenReturn("evento-de-teste");

                when(userRepository.getReferenceById(userId)).thenReturn(creator);
                when(ongRepository.getReferenceById(ongId)).thenReturn(ong);
                when(tagService.getOrCreateTags(any())).thenReturn(Set.of());
                when(eventRepository.existsBySlug("evento-de-teste")).thenReturn(false);
                when(eventMapper.toEntity(any(), eq("evento-de-teste"), eq(ong), eq(creator))).thenReturn(event);
                when(eventRepository.saveAndFlush(event)).thenReturn(event);

                EventResponseDTO expectedDto = mock(EventResponseDTO.class);
                when(eventMapper.toDto(event)).thenReturn(expectedDto);

                EventResponseDTO result = eventService.register(ongId, request);

                assertThat(result).isEqualTo(expectedDto);
                verify(eventRepository).saveAndFlush(event);
            }
        }

        /** Quando o slug normalizado já existe, deve ser gerado um sufixo aleatório. */
        @Test
        @DisplayName("deve gerar slug com sufixo quando o slug já existe")
        void shouldGenerateSlugWithSuffixWhenAlreadyExists() {
            Instant starts = Instant.now().plus(1, ChronoUnit.DAYS);
            RegisterEventRequestDTO request = buildRequest(starts, null);

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class);
                    MockedStatic<SlugUtils> slugUtils = mockStatic(SlugUtils.class)) {

                secUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                slugUtils.when(() -> SlugUtils.normalize(any())).thenReturn("evento-de-teste");
                slugUtils.when(() -> SlugUtils.withSuffix("evento-de-teste")).thenReturn("evento-de-teste-abc123");

                when(userRepository.getReferenceById(userId)).thenReturn(creator);
                when(ongRepository.getReferenceById(ongId)).thenReturn(ong);
                when(tagService.getOrCreateTags(any())).thenReturn(Set.of());
                when(eventRepository.existsBySlug("evento-de-teste")).thenReturn(true);
                when(eventMapper.toEntity(any(), eq("evento-de-teste-abc123"), eq(ong), eq(creator))).thenReturn(event);
                when(eventRepository.saveAndFlush(event)).thenReturn(event);
                when(eventMapper.toDto(event)).thenReturn(mock(EventResponseDTO.class));

                eventService.register(ongId, request);

                slugUtils.verify(() -> SlugUtils.withSuffix("evento-de-teste"));
            }
        }

        /**
         * Violação da constraint {@code uk_active_event_slug} no banco deve ser
         * convertida em {@link DuplicateResourceException}.
         */
        @Test
        @DisplayName("deve lançar DuplicateResourceException quando há violação de constraint de slug")
        void shouldThrowDuplicateResourceExceptionOnSlugConstraintViolation() {
            Instant starts = Instant.now().plus(1, ChronoUnit.DAYS);
            RegisterEventRequestDTO request = buildRequest(starts, null);

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class);
                    MockedStatic<SlugUtils> slugUtils = mockStatic(SlugUtils.class)) {

                secUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
                slugUtils.when(() -> SlugUtils.normalize(any())).thenReturn("slug");

                when(userRepository.getReferenceById(userId)).thenReturn(creator);
                when(ongRepository.getReferenceById(ongId)).thenReturn(ong);
                when(tagService.getOrCreateTags(any())).thenReturn(Set.of());
                when(eventRepository.existsBySlug(any())).thenReturn(false);
                when(eventMapper.toEntity(any(), any(), any(), any())).thenReturn(event);

                ConstraintViolationException cve = mock(ConstraintViolationException.class);
                when(cve.getConstraintName()).thenReturn("uk_active_event_slug");
                DataIntegrityViolationException ex = new DataIntegrityViolationException("slug", cve);
                when(eventRepository.saveAndFlush(event)).thenThrow(ex);

                assertThatThrownBy(() -> eventService.register(ongId, request))
                        .isInstanceOf(DuplicateResourceException.class)
                        .hasMessageContaining("Slug");
            }
        }

        /**
         * Data de início no passado deve ser rejeitada antes de qualquer persistência.
         */
        @Test
        @DisplayName("deve lançar DomainException quando a data de início está no passado")
        void shouldThrowDomainExceptionWhenStartsAtIsInThePast() {
            Instant pastDate = Instant.now().minus(1, ChronoUnit.HOURS);
            RegisterEventRequestDTO request = buildRequest(pastDate, Instant.now().plus(1, ChronoUnit.DAYS));

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class)) {
                secUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

                assertThatThrownBy(() -> eventService.register(ongId, request))
                        .isInstanceOf(DomainException.class)
                        .hasMessageContaining("passado");
            }
        }

        /**
         * Término anterior ao início deve ser rejeitado; ambos os instantes derivam da
         * mesma base.
         */
        @Test
        @DisplayName("deve lançar DomainException quando endsAt é anterior a startsAt")
        void shouldThrowDomainExceptionWhenEndsAtIsBeforeStartsAt() {
            Instant base = Instant.now();
            Instant starts = base.plus(2, ChronoUnit.DAYS);
            Instant ends = base.plus(1, ChronoUnit.DAYS);
            RegisterEventRequestDTO request = buildRequest(starts, ends);

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class)) {
                secUtils.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

                assertThatThrownBy(() -> eventService.register(ongId, request))
                        .isInstanceOf(DomainException.class)
                        .hasMessageContaining("anterior");
            }
        }
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    /**
     * Testes para {@link EventService#update(UUID, UpdateEventRequestDTO)}.
     *
     * <p>
     * Verifica atualização bem-sucedida e a exceção para IDs inexistentes.
     * </p>
     */
    @Nested
    @DisplayName("update()")
    class Update {

        /**
         * Evento existente deve ter seus dados mesclados e o DTO atualizado retornado.
         */
        @Test
        @DisplayName("deve atualizar evento existente com sucesso")
        void shouldUpdateEventSuccessfully() {
            Instant starts = Instant.now().plus(1, ChronoUnit.DAYS);
            UpdateEventRequestDTO request = new UpdateEventRequestDTO("Novo Título", "Nova desc", null, null, null,
                    null, null, null, null, starts, null);

            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
            when(eventRepository.save(event)).thenReturn(event);
            EventResponseDTO expectedDto = mock(EventResponseDTO.class);
            when(eventMapper.toDto(event)).thenReturn(expectedDto);

            EventResponseDTO result = eventService.update(eventId, request);

            assertThat(result).isEqualTo(expectedDto);
            verify(eventMapper).updateEntityFromDto(request, event);
        }

        /**
         * ID inexistente deve lançar {@link ResourceNotFoundException} sem tocar no
         * banco.
         */
        @Test
        @DisplayName("deve lançar ResourceNotFoundException para evento inexistente")
        void shouldThrowResourceNotFoundExceptionForUnknownEvent() {
            UpdateEventRequestDTO request = new UpdateEventRequestDTO("T", "D", null, null, null, null, null, null,
                    null, Instant.now().plusSeconds(60), null);
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventService.update(eventId, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // endEvent
    // -------------------------------------------------------------------------

    /**
     * Testes para {@link EventService#endEvent(UUID)}.
     *
     * <p>
     * Verifica que a transição de estado é delegada à entidade e que
     * IDs inválidos lançam a exceção correta.
     * </p>
     */
    @Nested
    @DisplayName("endEvent()")
    class EndEvent {

        /** Deve delegar o encerramento à entidade e persistir a alteração. */
        @Test
        @DisplayName("deve encerrar evento com sucesso")
        void shouldEndEventSuccessfully() {
            when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

            eventService.endEvent(eventId);

            verify(event).finish();
            verify(eventRepository).save(event);
        }

        /** ID inexistente deve lançar {@link ResourceNotFoundException}. */
        @Test
        @DisplayName("deve lançar ResourceNotFoundException para evento inexistente")
        void shouldThrowResourceNotFoundExceptionForUnknownEvent() {
            when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> eventService.endEvent(eventId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // -------------------------------------------------------------------------
    // listEvents
    // -------------------------------------------------------------------------

    /**
     * Testes para {@link EventService#listEvents(EventFilterDTO, Pageable)}.
     *
     * <p>
     * Verifica o enriquecimento em lote das contagens de voluntários e
     * o status de inscrição do usuário autenticado, evitando o problema N+1.
     * </p>
     */
    @Nested
    @DisplayName("listEvents()")
    class ListEvents {

        /**
         * Usuário autenticado deve ter a contagem de voluntários e o campo
         * {@code enrolled} preenchidos corretamente para cada evento da página.
         */
        @Test
        @DisplayName("deve retornar página enriquecida com contagens e status de inscrição")
        void shouldReturnEnrichedPage() {
            UUID ev1 = UUID.randomUUID();
            Event e1 = mock(Event.class);
            when(e1.getId()).thenReturn(ev1);

            Pageable pageable = PageRequest.of(0, 10);
            Page<Event> page = new PageImpl<>(List.of(e1), pageable, 1);
            EventFilterDTO filter = mock(EventFilterDTO.class);

            when(eventRepository.findAll(isA(Specification.class), eq(pageable))).thenReturn(page);
            List<Object[]> countResult = new ArrayList<>();
            countResult.add(new Object[] { ev1, 5L });
            when(eventVolunteerRepository.countVolunteersByEventIdsAndStatus(List.of(ev1),
                    VolunteerStatusEnum.CONFIRMED))
                    .thenReturn(countResult);

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class)) {
                secUtils.when(SecurityUtils::getCurrentUserIdOptional).thenReturn(Optional.of(userId));
                when(eventVolunteerRepository.findEventIdsByUserIdAndEventIds(userId, List.of(ev1)))
                        .thenReturn(List.of(ev1));

                EventListResponseDTO dto = mock(EventListResponseDTO.class);
                when(eventMapper.toListDto(e1, 5, true)).thenReturn(dto);

                Page<EventListResponseDTO> result = eventService.listEvents(filter, pageable);

                assertThat(result.getContent()).hasSize(1).containsExactly(dto);
            }
        }

        /**
         * Sem usuário autenticado, {@code enrolled} deve ser {@code false} para todos
         * os eventos e a query de inscrições não deve ser executada.
         */
        @Test
        @DisplayName("deve retornar enrolled=false para usuário não autenticado")
        void shouldReturnNotEnrolledWhenUnauthenticated() {
            UUID ev1 = UUID.randomUUID();
            Event e1 = mock(Event.class);
            when(e1.getId()).thenReturn(ev1);

            Pageable pageable = PageRequest.of(0, 10);
            Page<Event> page = new PageImpl<>(List.of(e1), pageable, 1);
            EventFilterDTO filter = mock(EventFilterDTO.class);

            when(eventRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
            when(eventVolunteerRepository.countVolunteersByEventIdsAndStatus(any(), any()))
                    .thenReturn(Collections.emptyList());

            try (MockedStatic<SecurityUtils> secUtils = mockStatic(SecurityUtils.class)) {
                secUtils.when(SecurityUtils::getCurrentUserIdOptional).thenReturn(Optional.empty());

                EventListResponseDTO dto = mock(EventListResponseDTO.class);
                when(eventMapper.toListDto(e1, 0, false)).thenReturn(dto);

                Page<EventListResponseDTO> result = eventService.listEvents(filter, pageable);

                assertThat(result.getContent()).containsExactly(dto);
                verify(eventVolunteerRepository, never()).findEventIdsByUserIdAndEventIds(any(), any());
            }
        }
    }
}