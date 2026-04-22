package com.techfun.altrua.features.ong.api.dto;

import java.math.BigDecimal;

import com.techfun.altrua.features.ong.domain.enums.OngStatusEnum;
import com.techfun.altrua.features.ong.domain.model.Ong;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Objeto de Transferência de Dados (DTO) para a requisição de registro de uma
 * nova ONG.
 * 
 * <p>
 * Define os campos necessários e as regras de validação para a criação de um
 * novo registro de organização no sistema.
 * </p>
 * 
 * @param name         Nome da organização (Obrigatório)
 * @param email        E-mail de contato válido (Obrigatório)
 * @param category     Categoria de atuação principal (Obrigatório)
 * @param cnpj         Cadastro Nacional da Pessoa Jurídica
 * @param description  Breve descrição sobre a ONG
 * @param phone        Telefone de contato
 * @param logoUrl      URL da imagem de logotipo
 * @param bannerUrl    URL da imagem de capa
 * @param donationInfo Texto explicativo sobre métodos de doação
 * @param latitude     Coordenada de latitude para geolocalização
 * @param longitude    Coordenada de longitude para geolocalização
 */
@Schema(description = "Dados de requisição para o registro de uma nova ONG")
public record RegisterOngRequestDTO(

        @Schema(description = "Nome oficial da organização", example = "Associação Amigos dos Animais", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O nome é obrigatório") String name,

        @Schema(description = "CNPJ da organização (apenas os 14 dígitos numéricos)", example = "12345678000199") @Pattern(regexp = "\\d{14}", message = "O CNPJ deve conter exatamente 14 dígitos numéricos") String cnpj,

        @Schema(description = "E-mail institucional de contato", example = "contato@amigosdosanimais.org", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "O email é obrigatório") @Email String email,

        @Schema(description = "Categoria de atuação principal", example = "Proteção Animal", requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank(message = "A categoria é obrigatória") String category,

        @Schema(description = "Breve descrição sobre a missão e atividades da ONG", example = "Resgate e acolhimento de animais abandonados.") String description,

        @Schema(description = "Telefone ou WhatsApp de contato", example = "11987654321") String phone,

        @Schema(description = "URL para o logotipo da organização", example = "https://link-da-imagem.com/logo.png") String logoUrl,

        @Schema(description = "URL para a imagem de capa ou banner", example = "https://link-da-imagem.com/banner.jpg") String bannerUrl,

        @Schema(description = "Texto explicativo sobre métodos de doação (PIX, conta bancária, etc.)", example = "Chave PIX: contato@amigosdosanimais.org") String donationInfo,

        @Schema(description = "Coordenada de latitude para geolocalização", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Coordenada de longitude para geolocalização", example = "-46.633308") BigDecimal longitude) {

    public Ong toEntity(String slug) {
        return Ong.builder()
                .name(this.name)
                .slug(slug)
                .status(OngStatusEnum.ACTIVE)
                .cnpj(this.cnpj)
                .email(this.email)
                .category(this.category)
                .description(this.description)
                .phone(this.phone)
                .logoUrl(this.logoUrl)
                .bannerUrl(this.bannerUrl)
                .donationInfo(this.donationInfo)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .build();
    }
}
