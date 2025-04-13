package me.itzisonn_.meazy.runtime.environment;

import java.util.Set;

/**
 * Represents global environment
 */
public interface GlobalEnvironment extends Environment, FunctionDeclarationEnvironment, ClassDeclarationEnvironment {
    /**
     * Adds to this global environment another global environment as related
     * @param globalEnvironment GlobalEnvironment to add
     */
    void addRelatedGlobalEnvironment(GlobalEnvironment globalEnvironment);

    /**
     * @return All related global environments
     */
    Set<GlobalEnvironment> getRelatedGlobalEnvironments();
}