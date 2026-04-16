package com.techfun.altrua.features.event.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/ongs/{ongId}/eventos")
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
    @PostMapping
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
     * Verifica se o usuário tem permissão de gerenciamento sobre o evento antes de
     * delegar a finalização ao serviço de domínio.
     * </p>
     *
     * @param eventId UUID do evento a ser encerrado.
     * @return Status 204 (No Content).
     */
    @Operation(summary = "Encerrar evento", description = "Finaliza um evento ativo. Requer permissão de gerenciamento sobre o evento específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento encerrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada: o evento não pode ser encerrado no status atual"),
            @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para gerenciar este evento"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado ou não pertence a esta ONG")
    })
    @PostMapping("/{eventId}/encerrar")
    @PreAuthorize("@securityService.canManageEvent(#eventId)")
    public ResponseEntity<Void> endEvent(
            @Parameter(description = "UUID da ONG proprietária") @PathVariable("ongId") UUID ongId,
            @Parameter(description = "UUID do evento a ser encerrado") @PathVariable UUID eventId) {
        eventService.endEvent(ongId, eventId);
        return ResponseEntity.noContent().build();
    }

}
