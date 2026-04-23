package com.techfun.altrua.features.event.api;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.event.api.dto.EventFilterDTO;
import com.techfun.altrua.features.event.api.dto.EventResponseDTO;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.service.EventService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST responsável pelo gerenciamento de eventos no contexto de uma
 * ONG.
 * 
 * <p>
 * Provê endpoints para criação, consulta e manipulação de eventos vinculados
 * a organizações específicas, garantindo as regras de autorização necessárias.
 * </p>
 */
@Tag(name = "Eventos", description = "Gerenciamento de eventos vinculados a ONGs")
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Registra um novo evento vinculado a uma ONG.
     * <p>
     * A autorização é validada via {@code SecurityService}, garantindo que o
     * usuário
     * autenticado seja administrador da ONG informada.
     * </p>
     *
     * @param ongId   UUID da ONG proprietária.
     * @param request Dados para criação do evento.
     * @return {@link EventResponseDTO} com o status 201 (Created).
     */
    @Operation(summary = "Registrar novo evento", description = "Cria um evento vinculado a uma ONG. Requer que o usuário seja administrador da ONG informada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou ausência de tags obrigatórias"),
            @ApiResponse(responseCode = "403", description = "Usuário não possui permissão de administrador na ONG"),
            @ApiResponse(responseCode = "404", description = "ONG não encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflito: Slug do evento já existe")
    })
    @PostMapping("/ongs/{ongId}/eventos")
    @PreAuthorize("@securityService.isOngAdmin(#ongId)")
    public ResponseEntity<EventResponseDTO> register(
            @Parameter(description = "UUID da ONG proprietária") @PathVariable("ongId") UUID ongId,
            @RequestBody @Valid RegisterEventRequestDTO request) {
        Event savedEvent = eventService.register(ongId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponseDTO.fromEntity(savedEvent));
    }

    /**
     * Finaliza um evento específico.
     * <p>
     * O acesso é restrito a administradores da ONG proprietária. A validação de
     * permissão
     * e a integridade da relação entre ONG e Evento são realizadas na camada de
     * segurança.
     * </p>
     *
     * @param ongId   UUID da ONG proprietária para validação de integridade.
     * @param eventId UUID do evento a ser encerrado.
     * @return Status 204 (No Content) em caso de sucesso.
     */
    @Operation(summary = "Encerrar evento", description = "Finaliza um evento ativo. Requer permissão de gerenciamento sobre o evento específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento encerrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada: o evento não pode ser encerrado no status atual"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para gerenciar este evento"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado ou não pertence a esta ONG")
    })
    @PostMapping("/ongs/{ongId}/eventos/{eventId}/encerrar")
    @PreAuthorize("@securityService.canManageEvent(#ongId, #eventId)")
    public ResponseEntity<Void> endEvent(
            @Parameter(description = "UUID da ONG proprietária") @PathVariable("ongId") UUID ongId,
            @Parameter(description = "UUID do evento a ser encerrado") @PathVariable("eventId") UUID eventId) {
        eventService.endEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista eventos de forma paginada com base em filtros dinâmicos.
     * <p>
     * Retorna apenas eventos que ainda não ocorreram (data de início no futuro).
     * Os filtros de tag, status e disponibilidade de voluntariado são opcionais.
     * </p>
     *
     * @param filter   DTO contendo os critérios de filtragem (tag, status,
     *                 acceptsVolunteers).
     * @param pageable Parâmetros de paginação e ordenação (padrão: 10 itens por
     *                 página).
     * @return {@link Page} contendo os eventos que atendem aos critérios.
     */
    @Operation(summary = "Listar eventos com filtros", description = "Recupera uma lista paginada de eventos futuros. Permite filtrar por tags, status e disponibilidade para voluntários.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação ou filtro inválidos")
    })
    @GetMapping("/eventos")
    public ResponseEntity<Page<EventResponseDTO>> list(
            @ParameterObject EventFilterDTO filter,
            @ParameterObject @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(eventService.listEvents(filter, pageable));
    }
}
