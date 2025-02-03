package me.itzisonn_.meazy.runtime.environment;

/**
 * ClassEnvironment represents environment for classes
 */
public interface ClassEnvironment extends Environment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    /**
     * @return ClassEnvironment's id
     */
    String getId();
}