package me.itzisonn_.meazy.runtime.environment;

/**
 * LoopEnvironment represents environment for loops
 */
public interface LoopEnvironment extends VariableDeclarationEnvironment {
    /**
     * Clears all declared variables
     */
    void clearVariables();
}