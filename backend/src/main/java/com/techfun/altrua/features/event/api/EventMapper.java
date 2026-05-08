package com.techfun.altrua.features.event.api;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.techfun.altrua.features.event.api.dto.EventListResponseDTO;
import com.techfun.altrua.features.event.api.dto.EventResponseDTO;
import com.techfun.altrua.features.event.api.dto.RegisterEventRequestDTO;
import com.techfun.altrua.features.event.domain.model.Event;
import com.techfun.altrua.features.ong.domain.model.Ong;
import com.techfun.altrua.features.tag.domain.Tag;
import com.techfun.altrua.features.user.domain.model.User;

/**
 * Mapper responsável pela conversão entre a entidade {@link Event} e seus
 * respectivos DTOs.
 * <p>
 * Utiliza o MapStruct para geração de código de mapeamento em tempo de
 * compilação,
 * garantindo alta performance e segurança de tipos.
 * </p>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    /**
     * Converte uma entidade {@link Event} para um DTO de resposta detalhado.
     * Mapeia o conjunto de objetos {@link Tag} para um conjunto de Strings contendo
     * apenas os nomes.
     *
     * @param event A entidade de origem vinda do banco de dados.
     * @return {@link EventResponseDTO} com os detalhes completos do evento.
     */
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagNames")
    EventResponseDTO toDto(Event event);

    /**
     * Converte uma entidade {@link Event} para um DTO resumido, ideal para
     * listagens em cards.
     * Realiza o mapeamento de propriedades aninhadas (Nome da ONG) e integra dados
     * calculados externamente.
     *
     * @param event                  A entidade de origem.
     * @param currentVolunteersCount Contador de voluntários processado pela camada
     *                               de serviço.
     * @return {@link EventListResponseDTO} contendo os dados essenciais para
     *         exibição em massa.
     */
    @Mapping(target = "ongName", source = "event.ong.name")
    @Mapping(target = "currentVolunteers", source = "currentVolunteersCount")
    EventListResponseDTO toListDto(Event event, Integer currentVolunteersCount);

    /**
     * Cria uma nova instância da entidade {@link Event} a partir de dados de
     * registro e contexto.
     * <p>
     * Este mapeador orquestra a fusão de múltiplos objetos de entrada, garantindo
     * que o status inicial
     * seja 'PUBLISHED' e que campos condicionais (como voluntários) sejam
     * sanitizados.
     * </p>
     *
     * @param dto     Dados da requisição enviados pelo cliente.
     * @param slug    Slug único gerado para identificação do evento na URL.
     * @param ong     Referência da ONG proprietária do evento.
     * @param creator Referência do usuário que está realizando o cadastro.
     * @return Entidade {@link Event} pronta para persistência.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", source = "slug")
    @Mapping(target = "ong", source = "ong")
    @Mapping(target = "createdByUser", source = "creator")
    @Mapping(target = "status", constant = "PUBLISHED")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "title", source = "dto.title")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "coverUrl", source = "dto.coverUrl")
    @Mapping(target = "externalLink", source = "dto.externalLink")
    @Mapping(target = "donationInfo", source = "dto.donationInfo")
    @Mapping(target = "donationExternalLink", source = "dto.donationExternalLink")
    @Mapping(target = "acceptsVolunteers", source = "dto.acceptsVolunteers")
    @Mapping(target = "latitude", source = "dto.latitude")
    @Mapping(target = "longitude", source = "dto.longitude")
    @Mapping(target = "addressLabel", source = "dto.addressLabel")
    @Mapping(target = "startsAt", source = "dto.startsAt")
    @Mapping(target = "endsAt", source = "dto.endsAt")
    @Mapping(target = "maxVolunteers", expression = "java(dto == null ? null : checkMaxVolunteers(dto.acceptsVolunteers(), dto.maxVolunteers()))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Event toEntity(RegisterEventRequestDTO dto, String slug, Ong ong, User creator);

    /**
     * Lógica de suporte para sanitização do campo de voluntários.
     * Garante que o número máximo de voluntários seja persistido apenas se o evento
     * aceitá-los.
     *
     * @param accepts Indicador se o evento aceita voluntários.
     * @param max     Número máximo de voluntários fornecido no DTO.
     * @return O valor de 'max' se 'accepts' for verdadeiro; caso contrário, nulo.
     */
    default Integer checkMaxVolunteers(Boolean accepts, Integer max) {
        if (Boolean.TRUE.equals(accepts)) {
            return max;
        }
        return null;
    }

    /**
     * Converte uma coleção de entidades {@link Tag} em um conjunto de nomes
     * formatados.
     * Utilizado via {@code qualifiedByName} no mapeamento de saída.
     *
     * @param tags Coleção de tags da entidade.
     * @return Conjunto de strings com os nomes das tags, ou conjunto vazio se nulo.
     */
    @Named("tagNames")
    default Set<String> tagNames(Set<Tag> tags) {
        if (tags == null) {
            return Collections.emptySet();
        }
        return tags.stream().map(Tag::getName).collect(Collectors.toSet());
    }
}