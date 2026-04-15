package com.techfun.altrua.features.event.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.Tag;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Objeto de Transferência de Dados (DTO) para representação de eventos.
 * 
 * <p>
 * Este record consolida as informações de um evento, incluindo dados de
 * localização,
 * voluntariado, doações e as tags associadas.
 * </p>
 *
 * @param id                   Identificador único do evento.
 * @param title                Título descritivo do evento.
 * @param description          Texto detalhado sobre o que ocorrerá no evento.
 * @param slug                 Identificador amigável para URLs.
 * @param coverUrl             URL da imagem de capa/banner do evento.
 * @param externalLink         Link externo para mais informações ou inscrições.
 * @param donationInfo         Texto informativo sobre como apoiar
 *                             financeiramente.
 * @param donationExternalLink Link direto para plataforma de doação externa.
 * @param acceptsVolunteers    Sinaliza se o evento está aberto a voluntariado.
 * @param maxVolunteers        Capacidade máxima de voluntários permitida.
 * @param latitude             Coordenada de latitude para geolocalização.
 * @param longitude            Coordenada de longitude para geolocalização.
 * @param addressLabel         Descrição textual do endereço (Ex: "Rua X, Número
 *                             Y").
 * @param startsAt             Instante de início do evento.
 * @param endsAt               Instante de término previsto.
 * @param tags                 Conjunto de nomes das tags (categorias)
 *                             vinculadas.
 * 
 * @see com.techfun.altrua.features.event.domain.model.Event
 */
@Schema(description = "Objeto de resposta com os detalhes completos de um evento")
public record EventResponseDTO(

        @Schema(description = "Identificador único do evento", example = "550e8400-e29b-411d-a716-446655440000") UUID id,

        @Schema(description = "Título descritivo do evento", example = "Mutirão de Reflorestamento Urbano") String title,

        @Schema(description = "Texto detalhado sobre o que ocorrerá no evento", example = "Evento destinado ao plantio de mudas nativas no parque central.") String description,

        @Schema(description = "Identificador amigável para URLs", example = "mutirao-reflorestamento-2026") String slug,

        @Schema(description = "URL da imagem de capa/banner", example = "https://cdn.ong.org/images/evento-01.jpg") String coverUrl,

        @Schema(description = "Link externo para mais informações", example = "https://ong.org/evento-detalhes") String externalLink,

        @Schema(description = "Informações sobre como apoiar financeiramente", example = "Aceitamos doações via PIX ou cartão.") String donationInfo,

        @Schema(description = "Link direto para plataforma de doação externa", example = "https://apoia.se/projeto-ong") String donationExternalLink,

        @Schema(description = "Indica se o evento aceita novos voluntários", example = "true") boolean acceptsVolunteers,

        @Schema(description = "Capacidade máxima de voluntários permitida", example = "50", nullable = true) Integer maxVolunteers,

        @Schema(description = "Latitude para geolocalização", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Longitude para geolocalização", example = "-46.633308") BigDecimal longitude,

        @Schema(description = "Descrição textual do endereço", example = "Rua das Palmeiras, 123, São Paulo - SP") String addressLabel,

        @Schema(description = "Instante de início do evento", example = "2026-05-20T09:00:00Z") Instant startsAt,

        @Schema(description = "Instante de término previsto", example = "2026-05-20T17:00:00Z") Instant endsAt,

        @Schema(description = "Conjunto de categorias vinculadas", example = "[\"meio ambiente\", \"sustentabilidade\"]") Set<String> tags) {

    /**
     * Converte uma entidade de domínio {@link Event} para seu respectivo DTO de
     * resposta.
     *
     * @param event A entidade original proveniente do banco de dados.
     * @return Uma nova instância de {@link EventResponseDTO} populada.
     */
    public static EventResponseDTO fromEntity(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getSlug(),
                event.getCoverUrl(),
                event.getExternalLink(),
                event.getDonationInfo(),
                event.getDonationExternalLink(),
                event.isAcceptsVolunteers(),
                event.getMaxVolunteers(),
                event.getLatitude(),
                event.getLongitude(),
                event.getAddressLabel(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
    }
}
