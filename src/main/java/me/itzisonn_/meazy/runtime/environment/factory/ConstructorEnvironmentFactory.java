package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * Represents factory for creating {@link ConstructorEnvironment}s
 */
@NullMarked
public interface ConstructorEnvironmentFactory { //TODO javadoc
    /**
     * Creates constructor environment
     *
     * @param parent Parent
     * @param isShared Whether constructor environment is shared
     * @return New constructor environment
     */
    ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, UUID startLabel, UUID endLabel, boolean isShared);

    /**
     * Creates non-shared constructor environment
     *
     * @param parent Parent
     * @return New constructor environment
     */
    ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, UUID startLabel, UUID endLabel);
}
