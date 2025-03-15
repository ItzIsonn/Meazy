package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.modifier.Modifier;

import java.util.HashSet;
import java.util.Set;

/**
 * ClassEnvironment represents environment for classes
 */
public interface ClassEnvironment extends Environment, FunctionDeclarationEnvironment, ConstructorDeclarationEnvironment {
    /**
     * @return ClassEnvironment's id
     */
    String getId();

    /**
     * @return All modifiers
     */
    Set<Modifier> getModifiers();



    /**
     * Adds given classEnvironment as base class
     *
     * @param classEnvironment ClassEnvironment to add
     */
    void addBaseClass(ClassEnvironment classEnvironment);

    /**
     * @param id Class's id
     * @return Base class of this ClassEnvironment with given id or null
     */
    default ClassEnvironment getBaseClass(String id) {
        for (ClassEnvironment classEnvironment : getBaseClasses()) {
            if (classEnvironment.getId().equals(id)) return classEnvironment;
        }

        return null;
    }

    /**
     * @param id Class's id
     * @return Base class of this ClassEnvironment and it's base classes with given id or null
     */
    default ClassEnvironment getDeepBaseClass(String id) {
        for (ClassEnvironment classEnvironment : getDeepBaseClasses()) {
            if (classEnvironment.getId().equals(id)) return classEnvironment;
        }

        return null;
    }

    /**
     * @return All base classes of this ClassEnvironment
     */
    Set<ClassEnvironment> getBaseClasses();

    /**
     * @return All base classes of this ClassEnvironment and it's base classes
     */
    default Set<ClassEnvironment> getDeepBaseClasses() {
        Set<ClassEnvironment> baseClasses = new HashSet<>();

        for (ClassEnvironment baseClass : getBaseClasses()) {
            baseClasses.add(baseClass);
            baseClasses.addAll(baseClass.getDeepBaseClasses());
        }

        return baseClasses;
    }
}