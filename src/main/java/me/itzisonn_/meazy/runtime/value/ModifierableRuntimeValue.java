package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.Environment;

import java.util.Set;

/**
 * Represents value in runtime that can have modifiers
 * @param <T> Type of stored value
 */
public interface ModifierableRuntimeValue<T> extends RuntimeValue<T> {
    /**
     * @param target Target modifier
     * @return Whether this runtime value has given modifier
     */
    default boolean hasModifier(Modifier target) {
        if (target == null) throw new NullPointerException("Target can't be null");

        for (Modifier modifier : getModifiers()) {
            if (modifier == target) return true;
        }

        return false;
    }

    /**
     * @param id Modifier's id
     * @return Whether this runtime value has modifier with given id
     */
    default boolean hasModifier(String id) {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (Modifier modifier : getModifiers()) {
            if (modifier.getId().equals(id)) return true;
        }

        return false;
    }

    /**
     * @return Modifiers
     */
    Set<Modifier> getModifiers();

    /**
     * @param environment From which environment access is checked
     * @return Whether this value is accessible from given environment
     */
    boolean isAccessible(Environment environment);
}
