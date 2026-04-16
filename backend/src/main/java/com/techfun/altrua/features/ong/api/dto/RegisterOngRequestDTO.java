package com.techfun.altrua.features.ong.api.dto;

import java.math.BigDecimal;

import com.techfun.altrua.features.ong.domain.enums.OngStatusEnum;
import com.techfun.altrua.features.ong.domain.model.Ong;

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
public record RegisterOngRequestDTO(

        @NotBlank(message = "Name is required") String name,
        @Pattern(regexp = "\\d{14}", message = "Invalid Format") String cnpj,
        @NotBlank(message = "Email is required") @Email String email,
        @NotBlank(message = "Category is required") String category,
        String description,
        String phone,
        String logoUrl,
        String bannerUrl,
        String donationInfo,
        BigDecimal latitude,
        BigDecimal longitude) {

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
