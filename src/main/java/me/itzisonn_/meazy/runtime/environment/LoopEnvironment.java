package me.itzisonn_.meazy.runtime.environment;

/**
 * Represents environment for loops
 */
public interface LoopEnvironment extends Environment {
    /**
     * Clears all declared variables
     */
    void clearVariables();
}