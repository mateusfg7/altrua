package com.techfun.altrua.features.ong.api;

import org.mapstruct.BeanMapping;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.techfun.altrua.features.ong.api.dto.OngResponseDTO;
import com.techfun.altrua.features.ong.api.dto.RegisterOngRequestDTO;
import com.techfun.altrua.features.ong.api.dto.UpdateOngRequestDTO;
import com.techfun.altrua.features.ong.domain.model.Ong;

/**
 * Mapper responsável pela transformação de dados entre a entidade {@link Ong} e
 * seus DTOs.
 * <p>
 * Centraliza a lógica de conversão para garantir que as regras de persistência
 * e os contratos da API permaneçam desacoplados.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
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

    /**
     * Mescla os dados de um {@link UpdateOngRequestDTO} em uma instância existente
     * de {@link Ong}.
     * <p>
     * Este método utiliza uma estratégia de atualização parcial:
     * <ul>
     * <li>Atributos de controle, identificadores e metadados de auditoria são
     * explicitamente ignorados.</li>
     * <li>Campos nulos no DTO são ignorados devido à estratégia
     * {@link NullValuePropertyMappingStrategy#IGNORE}.</li>
     * <li>Strings vazias ou contendo apenas espaços são ignoradas via método
     * {@link #isNotEmpty(String)}.</li>
     * </ul>
     * </p>
     *
     * @param dto O DTO contendo os dados de atualização.
     * @param ong A entidade persistida que receberá as modificações.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "cnpj", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "administrators", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateOngRequestDTO dto, @MappingTarget Ong ong);

    /**
     * Verificador de condição customizado para mapeamento de Strings.
     * <p>
     * Este método é utilizado automaticamente pelo MapStruct (devido à anotação
     * {@link Condition}) antes de mapear qualquer campo do tipo String. Ele impede
     * que valores vazios ou compostos apenas por espaços em branco sobrescrevam
     * dados válidos no banco de dados.
     * </p>
     *
     * @param value A string a ser validada.
     * @return {@code true} se a string não for nula e possuir conteúdo real;
     *         {@code false} caso contrário, abortando o mapeamento do campo
     *         específico.
     */
    @Condition
    default boolean isNotEmpty(String value) {
        return value != null && !value.trim().isBlank();
    }
}
