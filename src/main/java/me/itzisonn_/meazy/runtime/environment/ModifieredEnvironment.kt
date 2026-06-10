package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.modifier.Modifier;

import java.util.Set;

public interface ModifieredEnvironment extends Environment {
    /**
     * @return This class environment's modifiers
     */
    Set<Modifier> getModifiers();

    default boolean hasModifier(Modifier modifier) {
        return getModifiers().contains(modifier);
    }
}
