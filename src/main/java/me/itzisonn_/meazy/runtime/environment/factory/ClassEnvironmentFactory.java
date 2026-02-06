package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.runtime.environment.ClassDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, String id, @Nullable String baseClass, Set<Modifier> modifiers);

    /**
     * Creates class environment with empty modifiers
     *
     * @param parent Parent
     * @param isShared Whether class environment is shared
     * @param id Id
     * @return New class environment
     */
    ClassEnvironment create(ClassDeclarationEnvironment parent, boolean isShared, String id, @Nullable String baseClass);

    /**
     * Creates non-shared class environment
     *
     * @param parent Parent
     * @param id Whether class environment is shared
     * @param modifiers Modifiers
     * @return New class environment
     */
    ClassEnvironment create(ClassDeclarationEnvironment parent, String id, @Nullable String baseClass, Set<Modifier> modifiers);

    /**
     * Creates non-shared class environment with empty modifiers
     *
     * @param parent Parent
     * @param id Id
     * @return New class environment
     */
    ClassEnvironment create(ClassDeclarationEnvironment parent, String id, @Nullable String baseClass);
}
