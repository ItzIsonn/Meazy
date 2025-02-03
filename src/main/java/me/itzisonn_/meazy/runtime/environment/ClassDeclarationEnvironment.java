package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.values.classes.ClassValue;

import java.util.List;

/**
 * ClassDeclarationEnvironment adds the ability to declare and get classes
 */
public interface ClassDeclarationEnvironment extends Environment {
    /**
     * Declares given class in this environment
     *
     * @param value Class value
     */
    void declareClass(ClassValue value);

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     */
    default ClassValue getClass(String id) {
        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return classValue;
        }

        return null;
    }

    /**
     * @return All declared classes
     */
    List<ClassValue> getClasses();
}