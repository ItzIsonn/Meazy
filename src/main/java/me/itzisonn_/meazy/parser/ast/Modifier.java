package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.runtime.environment.Environment;

/**
 * Modifier defines behaviour of statements
 */
@Getter
public abstract class Modifier {
    /**
     * Modifier's id
     */
    private final String id;

    /**
     * Modifier constructor
     *
     * @param id Modifier's id
     */
    public Modifier(String id) {
        this.id = id;
    }

    /**
     * @param modifierStatement ModifierStatement with this Modifier
     * @param environment Environment which has given modifierStatement in it
     *
     * @return Whether can this Modifier be used on the given modifierStatement in given environment
     */
    public abstract boolean canUse(ModifierStatement modifierStatement, Environment environment);

    @Override
    public String toString() {
        return id;
    }
}