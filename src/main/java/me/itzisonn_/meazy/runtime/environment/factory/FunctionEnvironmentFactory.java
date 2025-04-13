package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;

/**
 * Represents factory for creating {@link FunctionEnvironment}s
 */
public interface FunctionEnvironmentFactory {
    /**
     * Creates function environment
     *
     * @param parent Parent
     * @param isShared Whether function environment is shared
     * @return New function environment
     */
    FunctionEnvironment create(FunctionDeclarationEnvironment parent, boolean isShared);

    /**
     * Creates non-shared function environment
     *
     * @param parent Parent
     * @return New function environment
     */
    FunctionEnvironment create(FunctionDeclarationEnvironment parent);
}
