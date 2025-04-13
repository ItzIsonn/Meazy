package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;

/**
 * Represents factory for creating {@link ConstructorEnvironment}s
 */
public interface ConstructorEnvironmentFactory {
    /**
     * Creates constructor environment
     *
     * @param parent Parent
     * @param isShared Whether constructor environment is shared
     * @return New constructor environment
     */
    ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, boolean isShared);

    /**
     * Creates non-shared constructor environment
     *
     * @param parent Parent
     * @return New constructor environment
     */
    ConstructorEnvironment create(ConstructorDeclarationEnvironment parent);
}
