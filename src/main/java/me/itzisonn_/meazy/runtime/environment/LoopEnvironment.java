package me.itzisonn_.meazy.runtime.environment;

/**
 * LoopEnvironment represents environment for loops
 */
public interface LoopEnvironment extends Environment {
    /**
     * Clears all declared variables
     */
    void clearVariables();
}