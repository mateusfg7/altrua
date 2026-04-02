package com.techfun.altrua.features.event.domain.model;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade que representa uma etiqueta ou categoria de classificação para
 * eventos.
 * 
 * <p>
 * As tags são utilizadas para facilitar a busca e filtragem de eventos por
 * interesses
 * comuns (ex: "Educação", "Meio Ambiente"). A unicidade é garantida pelo nome
 * normalizado.
 * </p>
 * 
 * @see Event
 */
@Entity
@Table(name = "tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    /** Identificador único da tag. */
    private UUID id;

    @Column(nullable = false, unique = true)
    @Setter(AccessLevel.NONE)
    /** Nome da tag em formato normalizado (lowercase e sem espaços extras). */
    private String name;

    /**
     * Cria uma nova tag garantindo a padronização do nome.
     * 
     * <p>
     * O nome é convertido para minúsculas e os espaços em branco nas extremidades
     * são removidos para evitar duplicidade semântica no banco de dados.
     * </p>
     *
     * @param name O nome descritivo da tag.
     */
    public Tag(String name) {
        this.name = name.toLowerCase(Locale.ROOT).trim();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Tag tag))
            return false;
        return Objects.equals(name, tag.name);
    }

}
