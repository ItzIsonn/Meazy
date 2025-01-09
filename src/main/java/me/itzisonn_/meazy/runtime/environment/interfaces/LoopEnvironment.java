package me.itzisonn_.meazy.runtime.environment.interfaces;

import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;

/**
 * LoopEnvironment represents environment for loops
 */
public interface LoopEnvironment extends VariableDeclarationEnvironment {
    /**
     * Clears all declared variables
     */
    void clearVariables();
}