package me.itzisonn_.meazy.lang;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;

/**
 * Represents language
 */
@Getter
@NullMarked
public class Language {
    private final String id;
    private final String name;

    /**
     * @param id Id
     * @param name Name
     * @throws IllegalArgumentException If given id is invalid
     */
    public Language(String id, String name) {
        if (!id.matches("[a-zA-Z_]+")) throw new IllegalArgumentException("Invalid id");

        this.id = id;
        this.name = name;
    }
}
