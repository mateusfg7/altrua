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
    @PostMapping
    @PreAuthorize("@securityService.isOngAdmin(#ongId)")
    public ResponseEntity<EventResponseDTO> register(@PathVariable("ongId") UUID ongId,
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
    @PostMapping("/{eventId}/encerrar")
    @PreAuthorize("@securityService.canManageEvent(#eventId)")
    public ResponseEntity<Void> endEvent(@PathVariable UUID eventId) {
        eventService.endEvent(eventId);
        return ResponseEntity.noContent().build();
    }

}
