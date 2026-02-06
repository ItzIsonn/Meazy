package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.VariableValue;
import me.itzisonn_.meazy.runtime.value.ClassValue;
import me.itzisonn_.meazy.runtime.value.FunctionValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.List;
import java.util.Map;

/**
 * Represents global environment
 */
@NullMarked
public interface FileEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ClassDeclarationEnvironment {
    /**
     * @return This environment's parent
     */
    @Override
    GlobalEnvironment getParent();



    /**
     * Adds to this global environment given nativeClass that is used by native statements
     */
    void addImport(String name);

    /**
     * @return All native classes
     */
    Map<String, ClassDesc> getImports();



    /**
     * @param id Variable's id
     * @return Declared variable with given id
     *
     * @apiNote Difference from {@link VariableDeclarationEnvironment#getVariable(String)} is that this method
     *          doesn't look for variable in related global environments
     */
    @Nullable
    VariableValue getLocalVariable(String id);

    /**
     * @param id Function's id
     * @param args Function's args
     * @return Declared function with given id and args or null
     *
     * @apiNote Difference from {@link FunctionDeclarationEnvironment#getFunction(String, List)} is that this method
     *          doesn't look for function in related global environments
     */
    @Nullable
    FunctionValue getLocalFunction(String id, List<ClassDesc> args);

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     *
     * @apiNote Difference from {@link ClassDeclarationEnvironment#getClass(String)} is that this method
     *          doesn't look for class in related global environments
     */
    @Nullable
    ClassValue getLocalClass(String id);
}