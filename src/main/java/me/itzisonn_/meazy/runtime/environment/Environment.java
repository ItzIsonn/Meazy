package me.itzisonn_.meazy.runtime.environment;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents an environment
 */
@NullMarked
public interface Environment {
    /**
     * @return Parent file of this environment
     */
    @Nullable
    default File getParentFile() {
        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getParentFile();
    }



    /**
     * @return This environment's parent
     */
    @Nullable
    Environment getParent();

    /**
     * Searches for given environment as a parent in this environment and all parents
     *
     * @param environment Environment to lookup
     * @return Whether this environment has requested parent
     */
    default boolean hasParent(Environment environment) {
        Environment parent = getParent();
        return environment.equals(parent) || parent != null && parent.hasParent(environment);
    }

    /**
     * Searches for environment as a parent that matches given predicate in this environment and all parents
     *
     * @param predicate Predicate that matches parent environment
     * @return Whether this environment has requested parent
     */
    default boolean hasParent(Predicate<@Nullable Environment> predicate) {
        Environment parent = getParent();
        return predicate.test(parent) || parent != null && parent.hasParent(predicate);
    }

    /**
     * Searches for environment as a parent that matches given predicate in this environment and all parents
     *
     * @param predicate Predicate that matches parent environment
     * @return Parent that matches given predicate or null
     */
    @Nullable
    default Environment getParent(Predicate<@Nullable Environment> predicate) {
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
        throw new IllegalStateException("Cannot get FileEnvironment from Environment");
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
     */
    @Nullable
    default VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) {
        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getVariableDeclarationEnvironment(id);
    }

    /**
     * Searches for function with given id and args in this environment and all parents
     *
     * @param id Function's id
     * @param parameters Function's parameters
     * @return Environment that has requested function or null
     */
    @Nullable
    default FunctionDeclarationEnvironment getFunctionDeclarationEnvironment(String id, List<ClassDesc> parameters) {
        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getFunctionDeclarationEnvironment(id, parameters);
    }
}