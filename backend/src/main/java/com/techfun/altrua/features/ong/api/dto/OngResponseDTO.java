package com.techfun.altrua.features.ong.api.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.techfun.altrua.features.ong.domain.model.Ong;

/**
 * Objeto de Transferência de Dados (DTO) para representação detalhada de uma
 * ONG nas respostas da API.
 * 
 * @param id           Identificador único da ONG
 * @param name         Nome da organização
 * @param slug         Identificador amigável para URL (gerado a partir do nome)
 * @param cnpj         Cadastro Nacional da Pessoa Jurídica
 * @param description  Descrição detalhada das atividades e missão da ONG
 * @param email        E-mail institucional de contato
 * @param phone        Telefone ou WhatsApp de contato
 * @param category     Categoria de atuação (ex: Proteção Animal, Educação)
 * @param status       Status atual do registro (valores possíveis: ATIVA,
 *                     INATIVA)
 * @param logoUrl      URL da imagem de logotipo
 * @param bannerUrl    URL da imagem de capa ou banner promocional
 * @param donationInfo Informações e instruções sobre como realizar doações
 * @param latitude     Coordenada geográfica de latitude para o mapa
 * @param longitude    Coordenada geográfica de longitude para o mapa
 * @param createdAt    Instante em que a ONG foi registrada no sistema
 * @param updatedAt    Instante da última atualização dos dados
 */
public record OngResponseDTO(
        UUID id,
        String name,
        String slug,
        String cnpj,
        String description,
        String email,
        String phone,
        String category,
        String status,
        String logoUrl,
        String bannerUrl,
        String donationInfo,
        BigDecimal latitude,
        BigDecimal longitude,
        Instant createdAt,
        Instant updatedAt) {

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
                ong.getCreatedAt(),
                ong.getUpdatedAt());
    }

}