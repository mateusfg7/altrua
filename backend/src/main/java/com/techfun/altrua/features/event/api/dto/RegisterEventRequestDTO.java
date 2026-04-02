package com.techfun.altrua.features.event.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.user.domain.User;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Data Transfer Object) para entrada de dados na criação de novos eventos.
 * 
 * <p>
 * Este record define os requisitos de validação para o cadastro de eventos por
 * uma ONG,
 * incluindo metadados geográficos, informações de voluntariado e doação.
 * </p>
 *
 * @param title                O título do evento. Não pode ser vazio.
 * @param description          Descrição detalhada sobre o evento.
 * @param coverUrl             URL da imagem de capa para o evento.
 * @param externalLink         Link para site externo ou formulário de inscrição
 *                             fora da plataforma.
 * @param donationInfo         Informações textuais sobre como contribuir
 *                             financeiramente.
 * @param donationExternalLink URL para plataforma externa de arrecadação.
 * @param acceptsVolunteers    Flag indicando se o evento aceita inscrições de
 *                             voluntários.
 * @param maxVolunteers        Limite máximo de voluntários (opcional).
 * @param latitude             Coordenada de latitude para o local do evento.
 * @param longitude            Coordenada de longitude para o local do evento.
 * @param addressLabel         Descrição legível do endereço (Ex: "Auditório
 *                             Principal").
 * @param startsAt             Instante exato de início do evento (formato
 *                             ISO-8601).
 * @param endsAt               Instante de encerramento do evento (opcional).
 * @param tags                 Conjunto de tags que categorizam o evento. Deve
 *                             conter ao menos um item.
 * 
 * @see com.techfun.altrua.features.event.domain.model.Event
 * @see com.techfun.altrua.features.event.domain.enums.EventStatusEnum
 */
public record RegisterEventRequestDTO(
        @NotBlank(message = "Title is required") String title,
        String description,
        String coverUrl,
        String externalLink,
        String donationInfo,
        String donationExternalLink,
        @NotNull(message = "Accepts volunteers is required") Boolean acceptsVolunteers,
        Integer maxVolunteers,
        BigDecimal latitude,
        BigDecimal longitude,
        String addressLabel,
        @NotNull(message = "Start date and time are required") Instant startsAt,
        Instant endsAt,
        @NotEmpty(message = "At least one tag must be provided") Set<@NotBlank(message = "Tag name is required") String> tags) {

    /**
     * Mapeia os dados do DTO para uma nova instância da entidade {@link Event}.
     * 
     * <p>
     * Por padrão, novos eventos são criados com o status {@code PUBLISHED}.
     * </p>
     *
     * @param slug    O slug único gerado previamente para o evento.
     * @param ong     A instância da {@link Ong} proprietária do evento.
     * @param creator O usuário ({@link User}) que está realizando o cadastro.
     * @return Uma instância de {@link Event} pronta para persistência.
     */
    public Event toEntity(String slug, Ong ong, User creator) {
        return Event.builder()
                .title(this.title)
                .description(this.description)
                .slug(slug)
                .ong(ong)
                .createdByUser(creator)
                .status(EventStatusEnum.PUBLISHED)
                .coverUrl(this.coverUrl)
                .externalLink(this.externalLink)
                .donationInfo(this.donationInfo)
                .donationExternalLink(this.donationExternalLink)
                .acceptsVolunteers(this.acceptsVolunteers)
                .maxVolunteers(this.maxVolunteers)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .addressLabel(this.addressLabel)
                .startsAt(this.startsAt)
                .endsAt(this.endsAt)
                .build();
    }
}
