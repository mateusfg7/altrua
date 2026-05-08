package com.techfun.altrua.features.ong.api.dto;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

/**
 * DTO para atualização de dados de uma ONG existente.
 * <p>
 * Este objeto é utilizado em operações de atualização parcial (PATCH).
 * Campos nulos serão ignorados durante o mapeamento, mantendo os valores
 * originais da entidade.
 * </p>
 *
 * @param name         Nome da organização.
 * @param description  Descrição detalhada das atividades e missão.
 * @param email        E-mail institucional para contato.
 * @param phone        Telefone ou WhatsApp de contato.
 * @param category     Área principal de atuação.
 * @param logoUrl      URL pública para a nova imagem do logotipo.
 * @param bannerUrl    URL pública para a nova imagem de banner.
 * @param donationInfo Instruções para doações e apoio financeiro.
 * @param latitude     Coordenada de latitude para geolocalização.
 * @param longitude    Coordenada de longitude para geolocalização.
 */
@Schema(description = "Objeto de requisição para atualização parcial de uma ONG. Apenas os campos enviados serão alterados.")
public record UpdateOngRequestDTO(

        @Schema(description = "Nome atualizado da organização", example = "Associação Amigos dos Animais") String name,

        @Schema(description = "Nova descrição das atividades e missão", example = "Nova descrição focada em reabilitação animal.") String description,

        @Email(message = "O e-mail deve ser válido") @Schema(description = "E-mail institucional atualizado", example = "novo.contato@amigosdosanimais.org") String email,

        @Schema(description = "Telefone ou WhatsApp atualizado", example = "11999998888") String phone,

        @Schema(description = "Nova área principal de atuação", example = "Educação Ambiental") String category,

        @URL(message = "A URL do logotipo deve ser válida") @Schema(description = "Nova URL pública para o logotipo", example = "https://cdn.ong.com/logos/novo-logo.png") String logoUrl,

        @URL(message = "A URL do banner deve ser válida") @Schema(description = "Nova URL pública para a imagem de capa", example = "https://cdn.ong.com/banners/novo-banner.jpg") String bannerUrl,

        @Schema(description = "Instruções de doação atualizadas", example = "Nova Chave PIX: financeiro@ong.org") String donationInfo,

        @Schema(description = "Latitude atualizada para geolocalização", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Longitude atualizada para geolocalização", example = "-46.633308") BigDecimal longitude) {
}
