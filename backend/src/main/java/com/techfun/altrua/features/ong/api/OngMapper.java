package com.techfun.altrua.features.ong.api;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.techfun.altrua.features.ong.api.dto.OngResponseDTO;
import com.techfun.altrua.features.ong.api.dto.RegisterOngRequestDTO;
import com.techfun.altrua.features.ong.domain.model.Ong;

/**
 * Mapper responsável pela transformação de dados entre a entidade {@link Ong} e
 * seus DTOs.
 * <p>
 * Centraliza a lógica de conversão para garantir que as regras de persistência
 * e os contratos da API permaneçam desacoplados.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OngMapper {

    /**
     * Converte a entidade de domínio para o DTO de resposta detalhado.
     * 
     * @param ong A entidade carregada do banco de dados.
     * @return {@link OngResponseDTO} contendo os dados públicos da organização.
     * @note O campo 'activeEventCount' assume 0 por padrão caso não seja mapeado
     *       explicitamente.
     */
    @Mapping(target = "activeEventCount", defaultValue = "0L")
    OngResponseDTO toDto(Ong ong);

    /**
     * Transforma os dados de registro em uma nova instância da entidade
     * {@link Ong}.
     * <p>
     * Este mapeamento aplica valores padrão de segurança para novas organizações,
     * como o status inicial 'ACTIVE', e ignora campos sensíveis ou gerados
     * automaticamente.
     * </p>
     * 
     * @param dto  Dados de entrada validados via {@link RegisterOngRequestDTO}.
     * @param slug Identificador textual único gerado para a URL da organização.
     * @return Entidade {@link Ong} configurada para o primeiro insert.
     */
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "administrators", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Ong toEntity(RegisterOngRequestDTO dto, String slug);
}
