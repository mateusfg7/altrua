package com.techfun.altrua.features.event.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO para a atualização dos dados de um evento existente.
 * <p>
 * Os campos são opcionais para permitir atualizações parciais, mas, quando
 * informados,
 * devem cumprir as regras de formatação e validação.
 * </p>
 *
 * @param title                Novo título descritivo do evento.
 * @param description          Nova descrição detalhada das atividades.
 * @param coverUrl             Nova URL da imagem de capa ou banner.
 * @param externalLink         Novo link para site externo ou formulário.
 * @param donationInfo         Novas informações sobre arrecadação.
 * @param donationExternalLink Novo link para plataforma de doação externa.
 * @param latitude             Nova coordenada de latitude.
 * @param longitude            Nova coordenada de longitude.
 * @param addressLabel         Novo nome do local ou endereço por extenso.
 * @param startsAt             Nova data e hora de início (ISO 8601).
 * @param endsAt               Nova data e hora de término (ISO 8601).
 */
@Schema(description = "Dados necessários para a atualização de um evento existente")
public record UpdateEventRequestDTO(

        @Schema(description = "Título descritivo do evento", example = "Mutirão de Limpeza de Praia - Edição Especial") @Size(min = 2, max = 100, message = "O nome deve ter no mínimo 2 caracteres") String title,

        @Schema(description = "Descrição detalhada sobre as atividades do evento", example = "Coleta seletiva e triagem de resíduos sólidos na orla.") String description,

        @URL(message = "A URL da imagem de capa deve ser válida") @Schema(description = "URL da imagem de capa ou banner", example = "https://cdn.ong.org/images/novo-banner.jpg") String coverUrl,

        @URL(message = "O link externo deve ser uma URL válida") @Schema(description = "Link para site externo ou formulário de inscrição", example = "https://ong.org/nova-info-evento") String externalLink,

        @Schema(description = "Instruções ou informações sobre doações", example = "As doações financeiras serão revertidas em mudas para plantio.") String donationInfo,

        @URL(message = "O link de doação deve ser uma URL válida") @Schema(description = "Link para plataforma de arrecadação externa", example = "https://doar.ong.org/nova-campanha") String donationExternalLink,

        @Schema(description = "Coordenada de latitude para o local do evento", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Coordenada de longitude para o local do evento", example = "-46.633308") BigDecimal longitude,

        @Schema(description = "Endereço por extenso ou nome do local", example = "Avenida Beira Mar, 1500, Centro") String addressLabel,

        @Schema(description = "Data e hora de início do evento (ISO 8601)", example = "2026-06-15T08:00:00Z") Instant startsAt,

        @Schema(description = "Data e hora de término prevista (ISO 8601)", example = "2026-06-15T12:00:00Z") Instant endsAt) {
}