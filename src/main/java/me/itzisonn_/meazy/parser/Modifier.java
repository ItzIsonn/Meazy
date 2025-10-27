package me.itzisonn_.meazy.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.ModifierStatement;
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.runtime.environment.Environment;

/**
 * Defines behaviour of statements
 */
@Getter
@EqualsAndHashCode
public abstract class Modifier {
    /**
     * Id
     */
    private final String id;

    /**
     * @param id Id
     * @throws NullPointerException If given id is null
     */
    public Modifier(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        this.id = id;
    }

    /**
     * @param modifierStatement Modifier statement with this modifier
     * @param context Runtime context
     * @param environment Environment that has given modifierStatement in it
     *
     * @return Whether this modifier can be used on the given modifierStatement in given environment
     */
    public abstract boolean canUse(ModifierStatement modifierStatement, RuntimeContext context, Environment environment);

    /**
     * @param requestEnvironment Environment from which asked an access
     * @param environment Environment that has ModifierStatement in it
     * @param identifier Identifier
     * @param hasModifier Whether object with given identifier has this modifier
     *
     * @return Whether can access object (variable, function, class, etc) with given identifier in given environment from requestEnvironment
     */
    public abstract boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier);

    @Override
    public String toString() {
        return id;
    }
}