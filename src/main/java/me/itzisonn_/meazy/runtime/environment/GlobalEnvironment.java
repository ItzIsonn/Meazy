package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.MeazyNativeClass;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;
import me.itzisonn_.meazy.runtime.value.function.FunctionValue;

import java.util.List;
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



    /**
     * Adds to this global environment given nativeClass that is used by native statements
     * @param nativeClass Class that is annotated with {@link MeazyNativeClass}
     */
    void addNativeClass(Class<?> nativeClass);

    /**
     * @return All native classes
     */
    Set<Class<?>> getNativeClasses();



    /**
     * @param id Variable's id
     * @return Declared variable with given id
     *
     * @throws NullPointerException If given id is null
     *
     * @apiNote Difference from {@link Environment#getVariable(String)} is that this method
     *          doesn't look for variable in related global environments
     */
    VariableValue getLocalVariable(String id) throws NullPointerException;

    /**
     * @param id Function's id
     * @param args Function's args
     * @return Declared function with given id and args or null
     *
     * @throws NullPointerException If either id or args is null
     *
     * @apiNote Difference from {@link FunctionDeclarationEnvironment#getFunction(String, List)} is that this method
     *          doesn't look for function in related global environments
     */
    FunctionValue getLocalFunction(String id, List<RuntimeValue<?>> args) throws NullPointerException;

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     *
     * @throws NullPointerException If given id is null
     *
     * @apiNote Difference from {@link ClassDeclarationEnvironment#getClass(String)} is that this method
     *          doesn't look for class in related global environments
     */
    ClassValue getLocalClass(String id) throws NullPointerException;
}