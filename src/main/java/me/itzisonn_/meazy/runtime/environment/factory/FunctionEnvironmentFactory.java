package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * Represents factory for creating {@link FunctionEnvironment}s
 */
@NullMarked
public interface FunctionEnvironmentFactory { //TODO javadoc
    /**
     * Creates function environment
     *
     * @param parent Parent
     * @param isShared Whether function environment is shared
     * @return New function environment
     */
    FunctionEnvironment create(FunctionDeclarationEnvironment parent, UUID startLabel, UUID endLabel, boolean isShared);

    /**
     * Creates non-shared function environment
     *
     * @param parent Parent
     * @return New function environment
     */
    FunctionEnvironment create(FunctionDeclarationEnvironment parent, UUID startLabel, UUID endLabel);
}
