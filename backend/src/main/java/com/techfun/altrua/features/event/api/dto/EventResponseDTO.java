package com.techfun.altrua.features.event.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.event.domain.model.Tag;

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
public record EventResponseDTO(
        UUID id,
        String title,
        String description,
        String slug,
        String coverUrl,
        String externalLink,
        String donationInfo,
        String donationExternalLink,
        boolean acceptsVolunteers,
        Integer maxVolunteers,
        BigDecimal latitude,
        BigDecimal longitude,
        String addressLabel,
        Instant startsAt,
        Instant endsAt,
        Set<String> tags) {

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
