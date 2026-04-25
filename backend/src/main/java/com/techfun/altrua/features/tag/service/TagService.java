package com.techfun.altrua.features.tag.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.features.tag.api.dto.TagResponseDTO;
import com.techfun.altrua.features.tag.domain.Tag;
import com.techfun.altrua.features.tag.repository.TagRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável pelo gerenciamento de etiquetas (tags) de eventos.
 *
 * <p>
 * Provê lógica para normalização de nomes e persistência idempotente,
 * garantindo que tags duplicadas (mesmo com variações de caixa ou espaços)
 * não sejam redundantes no sistema.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    /**
     * Garante a existência das tags fornecidas no banco de dados e as retorna como
     * entidades persistidas.
     *
     * <p>
     * Diferente de uma abordagem de verificação manual, este método utiliza uma
     * operação
     * atômica de "Upsert" (Insert on Conflict) para evitar condições de corrida
     * (race conditions)
     * em cenários de alta concorrência.
     * </p>
     *
     * <p>
     * O processo segue estas etapas:
     * </p>
     * <ol>
     * <li>Valida se o conjunto de entrada contém dados.</li>
     * <li>Normaliza os nomes (trim e lowercase) para manter a unicidade
     * semântica.</li>
     * <li>Executa uma inserção em lote que ignora conflitos de nomes já existentes
     * diretamente no banco.</li>
     * <li>Recupera o conjunto final de entidades sincronizadas com o estado atual
     * do banco.</li>
     * </ol>
     *
     * @param tags Conjunto de strings com os nomes das etiquetas.
     * @return Um {@link Set} de entidades {@link Tag} garantidamente persistidas.
     * @throws DomainException Caso o parâmetro {@code tags} seja nulo ou
     *                         vazio.
     */
    @Transactional
    public Set<Tag> getOrCreateTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new DomainException("É obrigatório informar ao menos uma tag.");
        }

        Set<String> normalizedNames = tags.stream()
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        tagRepository.ensureTagsExist(normalizedNames.toArray(new String[0]));

        return new HashSet<>(tagRepository.findAllByNameIn(normalizedNames));
    }

    /**
     * Recupera todas as etiquetas cadastradas e as converte para o formato de
     * resposta.
     * <p>
     * Este método obtém as entidades do banco de dados já ordenadas alfabeticamente
     * e realiza o mapeamento para {@link TagResponseDTO}, garantindo que a ordem
     * seja preservada para a correta exibição nos filtros da interface.
     * </p>
     *
     * @return Uma {@link List} de {@link TagResponseDTO} contendo o ID e o nome
     *         de todas as tags disponíveis.
     */
    public List<TagResponseDTO> listAllTags() {
        return tagRepository.findAllByOrderByNameAsc()
                .stream()
                .map(TagResponseDTO::fromEntity)
                .toList();
    }
}
