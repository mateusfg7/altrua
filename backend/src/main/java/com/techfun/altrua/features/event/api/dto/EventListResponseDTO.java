package com.techfun.altrua.features.event.api.dto;

import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.event.domain.model.Event;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para representação simplificada de eventos em
 * listagens.
 * <p>
 * Projetado especificamente para suprir as necessidades de visualização de
 * cards,
 * contendo informações resumidas e o estado atual de ocupação de vagas.
 * </p>
 *
 * @param id                Identificador único do evento.
 * @param title             Título público do evento.
 * @param slug              Identificador textual para URLs amigáveis.
 * @param description       Descrição curta ou resumo do evento.
 * @param coverUrl          URL da imagem de capa/banner.
 * @param ongName           Nome da organização responsável.
 * @param startsAt          Instante de início do evento.
 * @param addressLabel      Descrição textual do local (ex: Nome do Parque,
 *                          Cidade).
 * @param acceptsVolunteers Indica se o evento ainda está aberto a novas
 *                          inscrições.
 * @param currentVolunteers Número de voluntários com participação confirmada no
 *                          momento.
 * @param maxVolunteers     Capacidade máxima de voluntários definida para o
 *                          evento.
 */
@Schema(description = "Objeto de resposta resumido para listagem de eventos (Cards)")
public record EventListResponseDTO(

        @Schema(description = "Identificador único do evento", example = "550e8400-e29b-411d-a716-446655440000") UUID id,

        @Schema(description = "Título descritivo do evento", example = "Mutirão de Plantio no Parque Central") String title,

        @Schema(description = "Identificador amigável para URLs", example = "mutirao-plantio-parque-central") String slug,

        @Schema(description = "Breve descrição sobre o evento", example = "Venha ajudar a plantar 500 mudas nativas e contribuir para a recuperação da área verde.") String description,

        @Schema(description = "URL da imagem de capa ou banner", example = "https://cdn.ong.org/images/evento-01.jpg") String coverUrl,

        @Schema(description = "Nome da ONG realizadora", example = "Verde Vida") String ongName,

        @Schema(description = "Instante de início das atividades", example = "2026-03-25T08:00:00Z") Instant startsAt,

        @Schema(description = "Nome ou descrição do local", example = "Parque Central, São Paulo") String addressLabel,

        @Schema(description = "Indica se o evento aceita inscrição de voluntários", example = "true") boolean acceptsVolunteers,

        @Schema(description = "Número atual de voluntários confirmados", example = "32") Integer currentVolunteers,

        @Schema(description = "Capacidade total de voluntários", example = "50", nullable = true) Integer maxVolunteers) {

    /**
     * Mapeia uma entidade {@link Event} para {@link EventListResponseDTO}.
     * <p>
     * Este método exige que a contagem de voluntários seja fornecida externamente,
     * permitindo que o serviço gerencie a performance de contagem (ex: via queries
     * batch)
     * de forma independente da carga da entidade.
     * </p>
     *
     * @param event                  A entidade de origem contendo os dados básicos.
     * @param currentVolunteersCount O número total de voluntários confirmados para
     *                               este evento.
     * @return Uma nova instância de {@link EventListResponseDTO} populada.
     */
    public static EventListResponseDTO fromEntity(Event event, Integer currentVolunteersCount) {
        return new EventListResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getSlug(),
                event.getDescription(),
                event.getCoverUrl(),
                event.getOng().getName(),
                event.getStartsAt(),
                event.getAddressLabel(),
                event.isAcceptsVolunteers(),
                currentVolunteersCount,
                event.getMaxVolunteers());
    }
}
