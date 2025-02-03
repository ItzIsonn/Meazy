package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;
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
     * Returns environment that matches given predicate as a parent in this environment and all parents
     *
     * @param predicate Predicate that matches parent environment
     * @return Requested parent or null
     */
    default Environment getParent(Predicate<Environment> predicate) {
        Environment parent = getParent();
        if (predicate.test(parent)) return parent;
        if (parent != null) return parent.getParent(predicate);
        return null;
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
     * @return Whether is this environment shared
     */
    boolean isShared();



    /**
     * Declares variable with given data in this environment
     *
     * @param id Variable's id
     * @param dataType Variable's DataType
     * @param value Variable's value
     * @param isConstant Whether is this variable constant
     * @param accessModifiers Variable's access modifiers
     */
    void declareVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers);

    /**
     * Declares argument with given data in this environment
     *
     * @param id Argument's id
     * @param dataType Argument's DataType
     * @param value Argument's value
     * @param isConstant Whether is this argument constant
     * @param accessModifiers Argument's access modifiers
     */
    void declareArgument(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers);

    /**
     * Assigns value to existing non-constant variable
     *
     * @param id Variable's id
     * @param value Variable's new value
     */
    default void assignVariable(String id, RuntimeValue<?> value) {
        getVariable(id).setValue(value);
    }

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     */
    default RuntimeVariable getVariable(String id) {
        for (RuntimeVariable runtimeVariable : getVariables()) {
            if (runtimeVariable.getId().equals(id)) return runtimeVariable;
        }

        return null;
    }

    /**
     * @return All declared variables
     */
    List<RuntimeVariable> getVariables();



    /**
     * Searches for variable with given id in this environment and all parents
     *
     * @param id Variable's id
     * @return VariableDeclarationEnvironment that has requested variable or null
     */
    default Environment getVariableDeclarationEnvironment(String id) {
        if (getVariable(id) != null) return this;
        if (getParent() == null) return null;
        return getParent().getVariableDeclarationEnvironment(id);
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
}