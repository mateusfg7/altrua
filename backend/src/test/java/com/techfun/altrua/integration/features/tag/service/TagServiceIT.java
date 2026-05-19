package com.techfun.altrua.integration.features.tag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techfun.altrua.core.common.exceptions.DomainException;
import com.techfun.altrua.features.tag.api.dto.TagResponseDTO;
import com.techfun.altrua.features.tag.domain.Tag;
import com.techfun.altrua.features.tag.repository.TagRepository;
import com.techfun.altrua.features.tag.service.TagService;
import com.techfun.altrua.integration.IntegrationTestBase;

/**
 * Testes de integração para o gerenciamento de Tags.
 * Valida a persistência, normalização de nomes e garantias de não duplicidade
 * no banco de dados.
 */
@DisplayName("Integração: TagService")
class TagServiceIT extends IntegrationTestBase {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    // -------------------------------------------------------------------------
    // Cenários de sucesso
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Dado um set de tags válido")
    class Success {

        /**
         * Verifica se novas tags são corretamente mapeadas para a entidade e salvas.
         */
        @Test
        @DisplayName("deve criar tags novas e retorná-las persistidas")
        void shouldCreateAndReturnNewTags() {
            var result = tagService.getOrCreateTags(Set.of("educação", "saúde"));

            assertThat(result).hasSize(2);
            assertThat(result).extracting("name")
                    .containsExactlyInAnyOrder("educação", "saúde");
            assertThat(tagRepository.count()).isEqualTo(2);
        }

        /**
         * Garante que chamadas repetidas com o mesmo nome não gerem novos registros.
         */
        @Test
        @DisplayName("deve ser idempotente — não duplicar tags já existentes")
        void shouldBeIdempotent() {
            tagService.getOrCreateTags(Set.of("meio ambiente"));
            tagService.getOrCreateTags(Set.of("meio ambiente"));

            assertThat(tagRepository.count()).isEqualTo(1);
        }

        /**
         * Valida o tratamento de strings para manter a consistência dos dados.
         */
        @Test
        @DisplayName("deve normalizar nomes — trim e lowercase — evitando duplicatas semânticas")
        void shouldNormalizeNames() {
            var result = tagService.getOrCreateTags(
                    Set.of("  Meio Ambiente  ", "meio ambiente", "MEIO AMBIENTE"));

            assertThat(result).hasSize(1);
            assertThat(result.iterator().next().getName()).isEqualTo("meio ambiente");
            assertThat(tagRepository.count()).isEqualTo(1);
        }

        /**
         * Verifica se o serviço consegue identificar tags que já estão no banco e
         * criar apenas as faltantes.
         */
        @Test
        @DisplayName("deve retornar tags existentes misturadas com novas sem duplicar")
        void shouldReturnMixOfExistingAndNewTags() {
            tagRepository.save(new Tag("educação"));

            var result = tagService.getOrCreateTags(Set.of("educação", "saúde"));

            assertThat(result).hasSize(2);
            assertThat(tagRepository.count()).isEqualTo(2);
        }

        /**
         * Assegura a ordenação alfabética padrão para exibição em filtros ou listagens.
         */
        @Test
        @DisplayName("deve retornar todas as tags em ordem alfabética ao listar")
        void shouldReturnTagsInAlphabeticalOrder() {
            tagRepository.saveAll(
                    List.of(new Tag("saúde"), new Tag("educação"), new Tag("meio ambiente")));

            List<TagResponseDTO> result = tagService.listAllTags();

            assertThat(result).extracting("name")
                    .containsExactly("educação", "meio ambiente", "saúde");
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não houver tags no banco")
        void shouldReturnEmptyList_WhenNoTagsExist() {
            assertThat(tagService.listAllTags()).isEmpty();
        }
    }

    // -------------------------------------------------------------------------
    // Cenários de erro
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Dado um input inválido")
    class InvalidInput {

        /**
         * Valida a proteção contra coleções nulas no service.
         */
        @Test
        @DisplayName("deve lançar DomainException quando o set for nulo")
        void shouldThrowExceptionWhenSetIsNull() {
            assertThatThrownBy(() -> tagService.getOrCreateTags(null))
                    .isInstanceOf(DomainException.class);
        }

        /**
         * Valida que o processamento não deve ocorrer para coleções vazias.
         */
        @Test
        @DisplayName("deve lançar DomainException quando o set estiver vazio")
        void shouldThrowExceptionWhenSetIsEmpty() {
            assertThatThrownBy(() -> tagService.getOrCreateTags(Set.of()))
                    .isInstanceOf(DomainException.class);
        }
    }
}