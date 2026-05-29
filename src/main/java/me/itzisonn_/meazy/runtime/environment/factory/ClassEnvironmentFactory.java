package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.constant.ClassDesc;
import java.util.Set;

/**
 * Represents factory for creating {@link ClassEnvironment}s
 */
@NullMarked
public interface ClassEnvironmentFactory { //TODO javadoc for baseclass
    /**
     * Creates class environment
     *
     * @param parent Parent
     * @param isShared Whether class environment is shared
     * @param id Id
     * @param modifiers Modifiers
     * @return New class environment
     */
    ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, boolean isInterface, String id, @Nullable ClassDesc baseClass, Set<ClassDesc> interfaces, Set<Modifier> modifiers);

    /**
     * Creates class environment
     *
     * @param parent Parent
     * @param isShared Whether class environment is shared
     * @param id Id
     * @param modifiers Modifiers
     * @return New class environment
     */
    ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, boolean isInterface, String id, Set<String> unresolvedBaseClasses, Set<Modifier> modifiers);
}
