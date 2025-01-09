package me.itzisonn_.meazy.runtime.environment.interfaces;

import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;

/**
 * ClassEnvironment represents environment for classes
 */
public interface ClassEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    /**
     * @return ClassEnvironment's id
     */
    String getId();
}