package me.itzisonn_.meazy.runtime.environment;

import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;
import java.util.Map;

/**
 * Represents file environment
 */
@NullMarked
public interface FileEnvironment extends VariableDeclarationEnvironment, FunctionDeclarationEnvironment, ClassDeclarationEnvironment {
    @Override
    GlobalEnvironment getParent();



    /**
     * @return Package name of this File environment
     */
    String getPackageName();

    /**
     * @return Class name of this File environment
     */
    String getClassName();

    @Override
    String getFullClassName();



    /**
     * Adds to this file environment given import
     */
    void addImport(ClassDesc classDesc, String name);

    /**
     * Adds to this file environment given import
     */
    void addImport(String fullName);

    /**
     * @return All imports
     */
    Map<String, ClassDesc> getImports();
}