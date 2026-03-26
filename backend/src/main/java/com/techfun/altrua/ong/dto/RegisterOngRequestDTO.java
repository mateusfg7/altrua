package com.techfun.altrua.ong.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

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

                @NotBlank String name,

                @NotBlank @Email String email,

                @NotBlank String category,

                String cnpj,
                String description,
                String phone,
                String logoUrl,
                String bannerUrl,
                String donationInfo,
                BigDecimal latitude,
                BigDecimal longitude) {
}
