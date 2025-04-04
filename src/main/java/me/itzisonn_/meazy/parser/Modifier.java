package me.itzisonn_.meazy.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.runtime.environment.Environment;

/**
 * Defines behaviour of statements
 */
@Getter
@EqualsAndHashCode
public abstract class Modifier {
    /**
     * Modifier's id
     */
    private final String id;

    /**
     * Modifier constructor
     * @param id Modifier's id
     * @throws NullPointerException If given id is null
     */
    public Modifier(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        this.id = id;
    }

    /**
     * @param modifierStatement ModifierStatement with this Modifier
     * @param environment Environment that has given modifierStatement in it
     *
     * @return Whether this Modifier can be used on the given modifierStatement in given environment
     */
    public abstract boolean canUse(ModifierStatement modifierStatement, Environment environment);

    @Override
    public String toString() {
        return id;
    }
}