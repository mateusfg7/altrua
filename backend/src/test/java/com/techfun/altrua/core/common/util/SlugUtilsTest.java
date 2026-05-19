package com.techfun.altrua.core.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testes de unidade para a utilitária de geração de slugs.
 * Garante a correta higienização de strings e a unicidade via sufixos
 * aleatórios.
 */
@DisplayName("Utilitário: SlugUtils")
class SlugUtilsTest {

    // -------------------------------------------------------------------------
    // Normalização de Strings
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao normalizar strings")
    class Normalization {

        /**
         * Valida a transformação de textos diversos em slugs limpos.
         * Cobre: acentuação, espaços, caracteres especiais, emojis e hifens
         * redundantes.
         */
        @ParameterizedTest
        @CsvSource(delimiter = ';', value = {
                "Ação Social; acao-social",
                "ONG & Amigos!; ong-amigos",
                "  Espaços   Múltiplos  ; espacos-multiplos",
                "Criança Esperança 2026; crianca-esperanca-2026",
                "---Hifens---Iniciais---; hifens-iniciais",
                "L'Oréal; loreal",
                "Rua 25 de Março #10; rua-25-de-marco-10",
                "💡 ONG Eco; ong-eco",
                "!!! @@@; ''"
        })
        @DisplayName("deve converter input para formato slug minúsculo e sem acentos")
        void shouldNormalizeStringsCorrectly(String input, String expected) {
            String result = SlugUtils.normalize(input);
            assertEquals(expected, result);
        }
    }

    // -------------------------------------------------------------------------
    // Geração de Sufixos Aleatórios
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Ao adicionar sufixos")
    class Suffixing {

        /**
         * Verifica se o sufixo aleatório mantém o formato esperado (base-XXXX).
         */
        @Test
        @DisplayName("deve conter o prefixo original e comprimento adicional de 5 caracteres")
        void shouldAddRandomSuffixWithCorrectFormat() {
            String baseSlug = "minha-ong";
            String result = SlugUtils.withSuffix(baseSlug);

            assertTrue(result.startsWith("minha-ong-"));
            assertEquals(14, result.length()); // base(9) + hífen(1) + sufixo(4)
        }

        /**
         * Garante que a aleatoriedade impeça colisões em gerações consecutivas.
         */
        @Test
        @DisplayName("deve gerar sufixos distintos para a mesma base")
        void shouldGenerateDifferentSuffixes() {
            String baseSlug = "slug-teste";

            String first = SlugUtils.withSuffix(baseSlug);
            String second = SlugUtils.withSuffix(baseSlug);

            assertNotEquals(first, second);
        }

        /**
         * Valida a regra de limpeza que impede a formação de hifens duplos ("--").
         */
        @Test
        @DisplayName("deve evitar hifens duplos quando a base já termina com separador")
        void shouldAvoidDoubleHyphens() {
            String slugWithHyphen = "ong-";
            String result = SlugUtils.withSuffix(slugWithHyphen);

            assertFalse(result.contains("--"));
        }
    }
}