package me.itzisonn_.meazy.parser.modifier;

import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier;
import me.itzisonn_.meazy.parser.ast.statement.ModifierStatement;
import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

/**
 * Defines behavior of statements
 */
@NullMarked
public abstract class Modifier {
    /**
     * Id
     */
    private final String id;

    /**
     * @param id Id
     */
    public Modifier(String id) {
        this.id = id;
    }

    /**
     * @param modifierStatement Modifier statement with this modifier
     * @param environment       Environment that has given modifierStatement in it
     * @return Whether this modifier can be used on the given modifierStatement in given environment
     */
    public abstract boolean canUse(ModifierStatement modifierStatement, Environment environment);

    /**
     * @param requestEnvironment Environment from which asked an access
     * @param environment        Environment that has ModifierStatement in it
     * @param identifier         Identifier
     * @param hasModifier        Whether object with given identifier has this modifier
     * @return Whether can access object (variable, function, class, etc.) with given identifier in given environment from requestEnvironment
     */
    public abstract boolean canAccess(Environment requestEnvironment, Environment environment, Identifier identifier, boolean hasModifier);

    @Override
    public String toString() {
        return id;
    }

    public String getId() {
        return this.id;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Modifier)) return false;
        final Modifier other = (Modifier) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Modifier;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        return result;
    }
}