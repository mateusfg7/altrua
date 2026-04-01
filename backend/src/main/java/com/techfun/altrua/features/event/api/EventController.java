package com.techfun.altrua.features.event.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.event.api.dto.EventResponseDTO;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.service.EventService;
import com.techfun.altrua.infra.security.userdetails.UserPrincipal;

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
@RequestMapping("/ongs/{ongId}/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Registra um novo evento para uma ONG específica.
     * 
     * <p>
     * Este endpoint exige que o usuário esteja autenticado. A permissão de
     * administrador sobre a ONG informada é validada no nível de serviço.
     * </p>
     *
     * @param ongId         O UUID da ONG proprietária do evento.
     * @param request       Objeto contendo os dados de criação do evento.
     * @param userPrincipal O usuário autenticado extraído do contexto de segurança.
     * @return {@link ResponseEntity} contendo o DTO do evento criado e o status 201
     *         (Created).
     * 
     * @see com.techfun.altrua.features.event.service.EventService#register(UUID,
     *      RegisterEventRequestDTO, com.techfun.altrua.features.user.domain.User)
     */
    @PostMapping
    public ResponseEntity<EventResponseDTO> register(@PathVariable UUID ongId,
            @RequestBody @Valid RegisterEventRequestDTO request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Event savedEvent = eventService.register(ongId, request, userPrincipal.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponseDTO.fromEntity(savedEvent));
    }

}
