package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.ClassValue;

import java.util.Set;

/**
 * Adds to Environment ability to declare classes
 */
public interface ClassDeclarationEnvironment extends Environment {
    /**
     * Declares given class in this environment
     * @param value ClassValue
     */
    void declareClass(ClassValue value);

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     * @throws NullPointerException If given id is null
     */
    default ClassValue getClass(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return classValue;
        }

        return null;
    }

    /**
     * @return All declared classes
     */
    Set<ClassValue> getClasses();
}