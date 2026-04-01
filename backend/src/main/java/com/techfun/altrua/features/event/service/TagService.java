package com.techfun.altrua.features.event.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.techfun.altrua.features.event.domain.model.Tag;
import com.techfun.altrua.features.event.repository.TagRepository;

import jakarta.transaction.Transactional;
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
public class TagService {

    private final TagRepository tagRepository;

    /**
     * Recupera tags existentes no banco de dados ou cria novas instâncias para
     * termos inéditos.
     *
     * <p>
     * O processo de resolução segue estas etapas:
     * <ol>
     * <li>Valida se o conjunto de entrada não está vazio.</li>
     * <li>Normaliza os nomes para garantir consistência na busca.</li>
     * <li>Identifica quais tags já possuem registro via
     * {@link TagRepository#findAllByNameIn(Set)}.</li>
     * <li>Instancia e persiste apenas os termos que ainda não existem.</li>
     * <li>Retorna a união de tags pré-existentes e recém-criadas.</li>
     * </ol>
     * </p>
     *
     * @param tags Conjunto de strings representando os nomes das etiquetas.
     * @return Um {@link Set} de entidades {@link Tag} persistidas e prontas para
     *         associação.
     * @throws IllegalArgumentException Caso o parâmetro {@code tags} seja nulo ou
     *                                  vazio.
     * @see com.techfun.altrua.features.event.domain.model.Tag#Tag(String)
     */
    @Transactional
    public Set<Tag> getOrCreateTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("É obrigatório informar ao menos uma tag.");
        }

        Set<String> normalizedNames = tags.stream()
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        List<Tag> existingTags = tagRepository.findAllByNameIn(normalizedNames);

        existingTags.forEach(tag -> normalizedNames.remove(tag.getName()));

        if (!normalizedNames.isEmpty()) {
            List<Tag> newTags = normalizedNames.stream()
                    .map(Tag::new)
                    .toList();

            tagRepository.saveAll(newTags);

            Set<Tag> allTags = new HashSet<>(existingTags);
            allTags.addAll(newTags);

            return allTags;
        }

        return new HashSet<>(existingTags);
    }
}
