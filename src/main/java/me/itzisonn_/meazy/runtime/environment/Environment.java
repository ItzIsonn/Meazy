package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Represents an environment
 */
public interface Environment {
    /**
     * @return Parent file of this environment
     */
    default File getParentFile() {
        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getParentFile();
    }



    /**
     * @return This environment's parent
     */
    Environment getParent();

    /**
     * Searches for given environment as a parent in this environment and all parents
     *
     * @param environment Environment to lookup
     * @return Whether this environment has requested parent
     *
     * @throws NullPointerException If given environment is null
     */
    default boolean hasParent(Environment environment) throws NullPointerException {
        if (environment == null) throw new NullPointerException("Environment can't be null");

        Environment parent = getParent();
        if (environment.equals(parent)) return true;
        if (parent != null) return parent.hasParent(environment);
        return false;
    }

    /**
     * Searches for environment as a parent that matches given predicate in this environment and all parents
     *
     * @param predicate Predicate that matches parent environment
     * @return Whether this environment has requested parent
     *
     * @throws NullPointerException If given predicate is null
     */
    default boolean hasParent(Predicate<Environment> predicate) throws NullPointerException {
        if (predicate == null) throw new NullPointerException("Predicate can't be null");

        Environment parent = getParent();
        if (predicate.test(parent)) return true;
        if (parent != null) return parent.hasParent(predicate);
        return false;
    }

    /**
     * Searches for environment as a parent that matches given predicate in this environment and all parents
     *
     * @param predicate Predicate that matches parent environment
     * @return Parent that matches given predicate or null
     *
     * @throws NullPointerException If given predicate is null
     */
    default Environment getParent(Predicate<Environment> predicate) throws NullPointerException {
        if (predicate == null) throw new NullPointerException("Predicate can't be null");

        Environment parent = getParent();
        if (predicate.test(parent)) return parent;
        if (parent != null) return parent.getParent(predicate);
        return null;
    }

    default GlobalEnvironment getGlobalEnvironment() {
        Environment parent = getParent();
        if (parent instanceof GlobalEnvironment globalEnvironment) return globalEnvironment;
        if (parent != null) return parent.getGlobalEnvironment();
        return null;
    }



    /**
     * @return Whether this environment is shared
     */
    boolean isShared();



    /**
     * Declares given VariableValue in this environment
     * @param value VariableValue
     */
    void declareVariable(VariableValue value);

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     * @throws NullPointerException If given id is null
     */
    default VariableValue getVariable(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (VariableValue variableValue : getVariables()) {
            if (variableValue.getId().equals(id)) return variableValue;
        }

        return null;
    }

    /**
     * @return All declared variables
     */
    Set<VariableValue> getVariables();

    /**
     * Assigns value to existing non-constant variable
     *
     * @param id Variable's id
     * @param value Variable's new value
     *
     * @throws NullPointerException If either id or value is null
     * @throws InvalidIdentifierException If can't find variable with given id
     */
    default void assignVariable(String id, RuntimeValue<?> value) throws NullPointerException, InvalidIdentifierException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (value == null) throw new NullPointerException("Value can't be null");

        VariableValue variableValue = getVariable(id);
        if (variableValue == null) throw new InvalidIdentifierException("Can't find variable with id " + id);

        variableValue.setValue(value);
    }



    /**
     * Searches for variable with given id in this environment and all parents
     *
     * @param id Variable's id
     * @return Environment that has requested variable or null
     *
     * @throws NullPointerException If given id is null
     */
    default Environment getVariableDeclarationEnvironment(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        VariableValue variableValue = getVariable(id);
        if (variableValue != null) return variableValue.getParentEnvironment();

        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getVariableDeclarationEnvironment(id);
    }

    /**
     * Searches for function with given id and args in this environment and all parents
     *
     * @param id Function's id
     * @param args Function's args
     * @return Environment that has requested function or null
     *
     * @throws NullPointerException If either id or args is null
     */
    default FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<RuntimeValue<?>> args) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (args == null) throw new NullPointerException("Args can't be null");

        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getFunctionDeclarationEnvironment(id, args);
    }
}