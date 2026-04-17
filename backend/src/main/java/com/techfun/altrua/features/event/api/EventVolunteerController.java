package com.techfun.altrua.features.event.api;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techfun.altrua.features.event.service.EventVolunteerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Expõe os endpoints para que usuários realizem e gerenciem suas inscrições em
 * eventos de ONGs.
 * <p>
 * A classe atua como a camada de entrada para as operações de voluntariado,
 * traduzindo as requisições HTTP em chamadas de serviço e garantindo que o
 * contexto da ONG e do Evento sejam respeitados através dos caminhos da URL.
 * </p>
 */
@Tag(name = "Voluntários", description = "Inscrição e gerenciamento de voluntários em eventos")
@RestController
@RequestMapping("/ongs/{ongId}/eventos/{eventId}/volunteers")
@RequiredArgsConstructor
public class EventVolunteerController {

    private final EventVolunteerService eventVolunteerService;

    /**
     * Endpoint para processar a solicitação de inscrição do usuário autenticado.
     * <p>
     * Invoca a lógica de inscrição que valida capacidade e duplicidade sob controle
     * de transação.
     * </p>
     * 
     * @param ongId   Identificador da organização responsável.
     * @param eventId Identificador do evento alvo.
     * @return Resposta HTTP 201 em caso de sucesso.
     */
    @Operation(summary = "Inscrever-se em um evento", description = "Realiza a inscrição do usuário logado como voluntário em um evento específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscrição realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada (Evento lotado ou usuário já inscrito)"),
            @ApiResponse(responseCode = "404", description = "Evento ou ONG não encontrados"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
    })
    @PostMapping
    public ResponseEntity<Void> enroll(
            @Parameter(description = "ID da ONG proprietária do evento") @PathVariable("ongId") UUID ongId,
            @Parameter(description = "ID do evento") @PathVariable("eventId") UUID eventId) {
        eventVolunteerService.enroll(eventId, ongId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para revogar a participação de um voluntário.
     * <p>
     * O cancelamento é restrito ao vínculo específico entre o usuário logado e o
     * evento dentro do contexto da ONG informada.
     * </p>
     * 
     * @param ongId   Identificador da organização responsável.
     * @param eventId Identificador do evento do qual o usuário deseja sair.
     * @return Resposta HTTP 204 em caso de sucesso.
     */
    @Operation(summary = "Cancelar inscrição em um evento", description = "Remove a participação do usuário logado como voluntário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscrição cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Inscrição não encontrada para este evento no contexto desta ONG"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
    })
    @DeleteMapping
    public ResponseEntity<Void> unenroll(
            @Parameter(description = "ID da ONG proprietária do evento") @PathVariable("ongId") UUID ongId,
            @Parameter(description = "ID do evento no qual o usuário está inscrito") @PathVariable("eventId") UUID eventId) {
        eventVolunteerService.cancelEnrollment(eventId, ongId);
        return ResponseEntity.noContent().build();
    }
}
