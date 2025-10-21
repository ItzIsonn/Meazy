package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.io.File;
import java.util.List;
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

    /**
     * Finds FileEnvironment in parent environments of this environment
     * @return FileEnvironment
     */
    default FileEnvironment getFileEnvironment() {
        Environment parent = getParent();
        if (parent instanceof FileEnvironment fileEnvironment) return fileEnvironment;
        if (parent != null) return parent.getFileEnvironment();
        return null;
    }



    /**
     * @return Whether this environment is shared
     */
    boolean isShared();



    /**
     * Searches for variable with given id in this environment and all parents
     *
     * @param id Variable's id
     * @return Environment that has requested variable or null
     *
     * @throws NullPointerException If given id is null
     */
    default VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

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