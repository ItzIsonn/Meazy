package me.itzisonn_.meazy.parser.modifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.runtime.environment.Environment;

import java.util.function.BiFunction;

/**
 * Modifier defines behaviour of statements
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

    /**
     * @return Exact copy of this Modifier
     */
    public Modifier copy() {
        BiFunction<ModifierStatement, Environment, Boolean> function = this::canUse;

        return new Modifier(id) {
            @Override
            public boolean canUse(ModifierStatement modifierStatement, Environment environment) {
                return function.apply(modifierStatement, environment);
            }
        };
    }

    @Override
    public String toString() {
        return id;
    }
}