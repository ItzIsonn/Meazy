package me.itzisonn_.meazy.runtime.environment.interfaces.declaration;

import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.clazz.ClassValue;

import java.util.List;

/**
 * ClassDeclarationEnvironment adds the ability to declare and get classes
 */
public interface ClassDeclarationEnvironment extends Environment {
    /**
     * Declares given class in this environment
     *
     * @param id Class's id
     * @param value Class value
     */
    void declareClass(String id, ClassValue value);

    /**
     * @param id Class's id
     * @return Declared class with given id
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