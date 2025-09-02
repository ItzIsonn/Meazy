package me.itzisonn_.meazy.lang;

import lombok.Getter;

/**
 * Represents language
 */
@Getter
public class Language {
    private final String id;
    private final String name;

    /**
     * @param id Id
     * @param name Name
     * @throws NullPointerException If either id or name is null
     * @throws IllegalArgumentException If given id is invalid
     */
    public Language(String id, String name) {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (name == null) throw new NullPointerException("Name can't be null");

        if (!id.matches("[a-zA-Z_]+")) throw new IllegalArgumentException("Invalid id");

        this.id = id;
        this.name = name;
    }
}
