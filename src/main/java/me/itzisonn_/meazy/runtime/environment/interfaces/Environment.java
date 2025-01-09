package me.itzisonn_.meazy.runtime.environment.interfaces;

import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.function.Predicate;

/**
 * Environment
 */
public interface Environment {
    /**
     * @return Parent of this environment
     */
    Environment getParent();

    /**
     * Searches for given environment as a parent in this environment and all parents
     *
     * @param environment Environment to lookup
     * @return Whether has requested parent
     */
    default boolean hasParent(Environment environment) {
        Environment parent = getParent();
        if (environment.equals(parent)) return true;
        if (parent != null) return parent.hasParent(environment);
        return false;
    }

    /**
     * Searches for environment that matches given predicate as a parent in this environment and all parents
     *
     * @param predicate Predicate that matches parent environment
     * @return Whether has requested parent
     */
    default boolean hasParent(Predicate<Environment> predicate) {
        Environment parent = getParent();
        if (predicate.test(parent)) return true;
        if (parent != null) return parent.hasParent(predicate);
        return false;
    }



    /**
     * Searches for variable with given id in this environment and all parents
     *
     * @param id Variable's id
     * @return VariableDeclarationEnvironment that has requested variable
     */
    default VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) {
        Environment parent = getParent();
        if (parent == null) throw new InvalidIdentifierException("Variable with id " + id + " doesn't exist!");
        return parent.getVariableDeclarationEnvironment(id);
    }

    /**
     * Searches for function with given id and args in this environment and all parents
     *
     * @param id Function's id
     * @return FunctionDeclarationEnvironment that has requested function
     */
    default FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) {
        Environment parent = getParent();
        if (parent == null) throw new InvalidIdentifierException("Function with id " + id + " doesn't exist!");
        return parent.getFunctionDeclarationEnvironment(id, args);
    }



    /**
     * @return Whether is this environment shared
     */
    boolean isShared();
}