package com.techfun.altrua.features.ong.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.ong.domain.model.Ong;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Objeto de Transferência de Dados (DTO) para representação detalhada de uma
 * ONG nas respostas da API.
 * 
 * @param id               Identificador único da ONG
 * @param name             Nome da organização
 * @param slug             Identificador amigável para URL (gerado a partir do
 *                         nome)
 * @param cnpj             Cadastro Nacional da Pessoa Jurídica
 * @param description      Descrição detalhada das atividades e missão da ONG
 * @param email            E-mail institucional de contato
 * @param phone            Telefone ou WhatsApp de contato
 * @param category         Categoria de atuação (ex: Proteção Animal, Educação)
 * @param status           Status atual do registro (valores possíveis: ATIVA,
 *                         INATIVA)
 * @param logoUrl          URL da imagem de logotipo
 * @param bannerUrl        URL da imagem de capa ou banner promocional
 * @param donationInfo     Informações e instruções sobre como realizar doações
 * @param latitude         Coordenada geográfica de latitude para o mapa
 * @param longitude        Coordenada geográfica de longitude para o mapa
 * @param activeEventCount Número de eventos ativos associados à ONG
 * @param createdAt        Instante em que a ONG foi registrada no sistema
 */
@Schema(description = "Objeto de resposta com os detalhes completos de uma organização (ONG)")
public record OngResponseDTO(
        @Schema(description = "Identificador único da ONG no sistema", example = "550e8400-e29b-411d-a716-446655440000") UUID id,

        @Schema(description = "Nome oficial da organização", example = "Associação Amigos dos Animais") String name,

        @Schema(description = "Identificador amigável gerado para uso em URLs", example = "amigos-dos-animais") String slug,

        @Schema(description = "Número do CNPJ", example = "12345678000199") String cnpj,

        @Schema(description = "Missão, visão e descrição detalhada das atividades", example = "Atuamos no resgate e reabilitação de animais abandonados desde 2010.") String description,

        @Schema(description = "E-mail institucional para contato", example = "contato@amigosdosanimais.org") String email,

        @Schema(description = "Telefone ou WhatsApp de contato", example = "11987654321") String phone,

        @Schema(description = "Área principal de atuação da ONG", example = "Proteção Animal") String category,

        @Schema(description = "Estado atual do registro no sistema (Definido automaticamente como ACTIVE na criação)", allowableValues = {
                "ACTIVE", "INACTIVE" }, example = "ACTIVE", accessMode = Schema.AccessMode.READ_ONLY) String status,

        @Schema(description = "URL pública para o logotipo da ONG", example = "https://cdn.ong.com/logos/amigos-animais.png") String logoUrl,

        @Schema(description = "URL pública para a imagem de capa ou banner", example = "https://cdn.ong.com/banners/banner-resgate.jpg") String bannerUrl,

        @Schema(description = "Instruções detalhadas para doadores (PIX, contas bancárias, etc)", example = "Chave PIX: financeiro@amigosdosanimais.org") String donationInfo,

        @Schema(description = "Coordenada de latitude para geolocalização", example = "-23.550520") BigDecimal latitude,

        @Schema(description = "Coordenada de longitude para geolocalização", example = "-46.633308") BigDecimal longitude,

        @Schema(description = "Número de eventos ativos (campo calculado via subquery). Não é editável.", example = "5", accessMode = Schema.AccessMode.READ_ONLY) Long activeEventCount,

        @Schema(description = "Data e hora em que o registro foi criado", accessMode = Schema.AccessMode.READ_ONLY) Instant createdAt) {

    /**
     * Converte uma instância da entidade {@link Ong} para {@link OngResponseDTO}.
     *
     * @param ong a entidade original proveniente do banco de dados
     * @return uma nova instância de DTO com os dados mapeados
     */
    public static OngResponseDTO fromEntity(Ong ong) {
        return new OngResponseDTO(
                ong.getId(),
                ong.getName(),
                ong.getSlug(),
                ong.getCnpj(),
                ong.getDescription(),
                ong.getEmail(),
                ong.getPhone(),
                ong.getCategory(),
                ong.getStatus().name(),
                ong.getLogoUrl(),
                ong.getBannerUrl(),
                ong.getDonationInfo(),
                ong.getLatitude(),
                ong.getLongitude(),
                ong.getActiveEventCount() != null ? ong.getActiveEventCount() : 0L,
                ong.getCreatedAt());
    }

}