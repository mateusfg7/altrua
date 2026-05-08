package com.techfun.altrua.features.event.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para o registro de novos eventos vinculados a uma ONG.
 * <p>
 * Este objeto contém todas as informações necessárias para a criação de um
 * evento,
 * incluindo dados de localização, cronograma e regras para voluntariado.
 * </p>
 *
 * @param title                Título descritivo do evento.
 * @param description          Descrição detalhada das atividades.
 * @param coverUrl             URL da imagem de capa ou banner.
 * @param externalLink         Link para site externo ou formulário.
 * @param donationInfo         Informações sobre arrecadação.
 * @param donationExternalLink Link para plataforma de doação externa.
 * @param acceptsVolunteers    Indica se o evento recebe voluntários.
 * @param maxVolunteers        Limite máximo de voluntários.
 * @param latitude             Coordenada de latitude.
 * @param longitude            Coordenada de longitude.
 * @param addressLabel         Nome do local ou endereço por extenso.
 * @param startsAt             Data e hora de início (ISO 8601).
 * @param endsAt               Data e hora de término (ISO 8601).
 * @param tags                 Lista de tags para categorização.
 */
@Schema(description = "Dados necessários para o registro de um novo evento")
public record RegisterEventRequestDTO(

        @Schema(description = "Título descritivo do evento", example = "Mutirão de Limpeza de Praia", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O título é obrigatório") String title,

        @Schema(description = "Descrição detalhada sobre as atividades do evento", example = "Coleta de resíduos sólidos na orla da Praia Central.") String description,

        @URL(message = "A URL da imagem de capa deve ser válida") @Schema(description = "URL da imagem de capa ou banner", example = "https://cdn.ong.org/images/banner-evento.jpg") String coverUrl,

        @URL(message = "O link externo deve ser uma URL válida") @Schema(description = "Link para site externo ou formulário de inscrição", example = "https://ong.org/info-evento") String externalLink,

        @Schema(description = "Instruções ou informações sobre doações", example = "As doações serão destinadas à compra de sacos de lixo e luvas.") String donationInfo,

        @URL(message = "O link de doação deve ser uma URL válida") @Schema(description = "Link para plataforma de arrecadação externa", example = "https://doar.ong.org/campanha-limpeza") String donationExternalLink,

        @Schema(description = "Define se o evento está aberto para voluntários", example = "true", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "A indicação de aceitação de voluntários é obrigatória") Boolean acceptsVolunteers,

        @Schema(description = "Número máximo de voluntários permitidos", example = "100") Integer maxVolunteers,

        @Schema(description = "Coordenada de latitude para o local do evento", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Coordenada de longitude para o local do evento", example = "-46.633308") BigDecimal longitude,

        @Schema(description = "Endereço por extenso ou nome do local", example = "Avenida Beira Mar, S/N, Centro") String addressLabel,

        @Schema(description = "Data e hora de início do evento (ISO 8601)", example = "2026-06-15T08:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED) @NotNull(message = "A data e hora de início são obrigatórias") Instant startsAt,

        @Schema(description = "Data e hora de término prevista (ISO 8601)", example = "2026-06-15T12:00:00Z") Instant endsAt,

        @Schema(description = "Conjunto de nomes das tags para categorização", example = "[\"Meio Ambiente\", \"Limpeza\"]", requiredMode = Schema.RequiredMode.REQUIRED) @NotEmpty(message = "É obrigatório informar ao menos uma tag") Set<@NotBlank(message = "O nome da tag não pode estar em branco") String> tags) {
}
