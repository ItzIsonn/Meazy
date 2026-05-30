package me.itzisonn_.meazy.runtime.environment;

import org.jspecify.annotations.NullMarked;

/**
 * Represents environment for constructors
 */
@NullMarked
public interface ConstructorEnvironment extends LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    @Override
    ConstructorDeclarationEnvironment getParent();
}