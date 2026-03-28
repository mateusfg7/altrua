package com.techfun.altrua.common.util;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Locale;

/**
 * Classe utilitária para manipulação e geração de slugs amigáveis para URLs.
 * 
 * <p>
 * Transforma nomes arbitrários em strings simplificadas, sem acentos ou
 * caracteres
 * especiais, adequadas para identificadores únicos em caminhos de URI.
 * </p>
 */
public final class SlugUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * Construtor privado para reforçar o padrão de classe utilitária,
     * impedindo a instanciação.
     */
    private SlugUtils() {
    }

    /**
     * Normaliza uma string de texto para o formato de slug.
     * 
     * <p>
     * Realiza a remoção de acentuação, conversão para minúsculas, remoção de
     * caracteres
     * não alfanuméricos e substituição de espaços por hifens.
     * </p>
     * 
     * @param name o texto original (ex: nome da ONG)
     * @return a string normalizada (ex: "nome-da-ong")
     */
    public static String normalize(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase(Locale.ROOT)
                .strip()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s-]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Adiciona um sufixo aleatório de 4 caracteres a um slug base.
     * 
     * <p>
     * Utilizado para garantir a unicidade do slug caso o nome base já exista no
     * sistema.
     * </p>
     * 
     * @param slug o slug base já normalizado
     * @return o slug original seguido de um hífen e um sufixo alfanumérico
     *         aleatório
     */
    public static String withSuffix(String slug) {
        char[] suffix = new char[4];
        for (int i = 0; i < 4; i++) {
            suffix[i] = CHARS.charAt(RANDOM.nextInt(CHARS.length()));
        }
        return slug + "-" + new String(suffix);
    }

}
