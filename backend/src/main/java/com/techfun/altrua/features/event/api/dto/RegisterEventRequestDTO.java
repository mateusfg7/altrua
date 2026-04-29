package com.techfun.altrua.features.event.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import com.techfun.altrua.features.event.domain.enums.EventStatusEnum;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.user.domain.model.User;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
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
@Schema(description = "Dados necessários para o registro de um novo evento")
public record RegisterEventRequestDTO(

        @Schema(description = "Título descritivo do evento", example = "Mutirão de Limpeza de Praia") @NotBlank(message = "O título é obrigatório") String title,

        @Schema(description = "Descrição detalhada sobre as atividades do evento", example = "Coleta de resíduos sólidos na orla da Praia Central.") String description,

        @Schema(description = "URL da imagem de capa ou banner", example = "https://cdn.ong.org/images/banner-evento.jpg") String coverUrl,

        @Schema(description = "Link para site externo ou formulário de inscrição", example = "https://ong.org/info-evento") String externalLink,

        @Schema(description = "Instruções ou informações sobre doações", example = "As doações serão destinadas à compra de sacos de lixo e luvas.") String donationInfo,

        @Schema(description = "Link para plataforma de arrecadação externa", example = "https://doar.ong.org/campanha-limpeza") String donationExternalLink,

        @Schema(description = "Define se o evento está aberto para voluntários", example = "true") @NotNull(message = "A indicação de aceitação de voluntários é obrigatória") Boolean acceptsVolunteers,

        @Schema(description = "Número máximo de voluntários permitidos (caso o evento aceitar voluntários)", example = "100") Integer maxVolunteers,

        @Schema(description = "Coordenada de latitude para o local do evento", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Coordenada de longitude para o local do evento", example = "-46.633308") BigDecimal longitude,

        @Schema(description = "Endereço por extenso ou nome do local", example = "Avenida Beira Mar, S/N, Centro") String addressLabel,

        @Schema(description = "Data e hora de início do evento (ISO 8601)", example = "2026-06-15T08:00:00Z") @NotNull(message = "A data e hora de início são obrigatórias") Instant startsAt,

        @Schema(description = "Data e hora de término prevista (ISO 8601)", example = "2026-06-15T12:00:00Z") Instant endsAt,

        @Schema(description = "Conjunto de nomes das tags para categorização", example = "[\"Meio Ambiente\", \"Limpeza\"]") @NotEmpty(message = "É obrigatório informar ao menos uma tag") Set<@NotBlank(message = "O nome da tag não pode estar em branco") String> tags) {

    /**
     * Valida a consistência entre {@code acceptsVolunteers} e
     * {@code maxVolunteers}.
     * <p>
     * Quando o evento aceita voluntários, {@code maxVolunteers} deve ser informado
     * e conter um valor maior que zero. Caso {@code acceptsVolunteers} seja
     * {@code false} ou {@code null}, o campo é ignorado.
     * </p>
     *
     * @return {@code true} se a combinação dos campos for válida; {@code false}
     *         caso contrário.
     */
    @AssertTrue(message = "Número máximo de voluntários é obrigatório e deve ser maior que zero quando o evento aceitar voluntários")
    private boolean isMaxVolunteersValid() {
        return !Boolean.TRUE.equals(acceptsVolunteers) || maxVolunteers != null && maxVolunteers > 0;
    }

    /**
     * Mapeia os dados do DTO para uma nova instância da entidade {@link Event}.
     * 
     * <p>
     * Por padrão, novos eventos são criados com o status {@code PUBLISHED}.
     * </p>
     *
     * <p>
     * O campo {@code maxVolunteers} só é repassado à entidade quando
     * {@code acceptsVolunteers} for {@code true}; caso contrário, é persistido
     * como {@code null}, independentemente do valor informado na requisição.
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
                .maxVolunteers(Boolean.TRUE.equals(this.acceptsVolunteers) ? this.maxVolunteers : null)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .addressLabel(this.addressLabel)
                .startsAt(this.startsAt)
                .endsAt(this.endsAt)
                .build();
    }
}
