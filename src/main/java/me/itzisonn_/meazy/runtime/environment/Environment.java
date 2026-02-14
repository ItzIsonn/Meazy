package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.ClassValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents an environment
 */
@NullMarked
public interface Environment {
    /**
     * @return Parent file of this environment //TODO
     */
    default String getPackageName() {
        Environment parent = getParent();
        if (parent == null) throw new NullPointerException("Parent is null");
        return parent.getPackageName();
    }

    @Nullable
    default String getClassName() {
        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getClassName();
    }

    default ClassDesc resolveClassDesc(ClassDesc classDesc) {
        if (classDesc.isPrimitive()) return classDesc;

        ClassDeclarationEnvironment classDeclarationEnvironment = getClassDeclarationEnvironment(classDesc);
        if (classDeclarationEnvironment == null) throw new RuntimeException("Can't find class " + classDesc);

        ClassValue classValue = classDeclarationEnvironment.getClass(classDesc.displayName());
        if (classValue == null) throw new RuntimeException("Can't find class " + classDesc);

        return classValue.asClassDesc();
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
    default boolean isShared() {
        Environment parent = getParent();
        if (parent == null) return false;
        return parent.isShared();
    }



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

    //TODO
    @Nullable
    default ClassDeclarationEnvironment getClassDeclarationEnvironment(String id) {
        return getClassDeclarationEnvironment(ClassDesc.of(id));
    }

    //TODO
    @Nullable
    default ClassDeclarationEnvironment getClassDeclarationEnvironment(ClassDesc classDesc) {
        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getClassDeclarationEnvironment(classDesc);
    }

    @Nullable
    default ClassValue getClassValue(ClassDesc classDesc) {
        ClassDeclarationEnvironment classDeclarationEnvironment = getClassDeclarationEnvironment(classDesc);
        if (classDeclarationEnvironment == null) return null;
        return classDeclarationEnvironment.getClass(classDesc.displayName());
    }

    default boolean isInstanceOf(ClassDesc classDesc, ClassDesc target) {
        if (classDesc.equals(target)) return true;

        ClassValue classValue = getClassValue(classDesc);
        if (classValue == null) return false;

        if (classValue.getEnvironment().getBaseClass() != null && isInstanceOf(classValue.getEnvironment().getBaseClass(), target)) return true;

        for (ClassDesc interfaceClassDesc : classValue.getEnvironment().getInterfaces()) {
            if (isInstanceOf(interfaceClassDesc, target)) return true;
        }

        return false;
    }
}