package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.ClassValue;
import org.jspecify.annotations.NullMarked;

import java.util.Optional;
import java.util.Set;

/**
 * Adds to Environment ability to declare classes
 */
@NullMarked
public interface ClassDeclarationEnvironment extends Environment {
    /**
     * Declares given class in this environment
     * TODO
     */
    ClassValue declareClass(ClassEnvironment classEnvironment);

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     */
    default Optional<ClassValue> getClass(String id) {
        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return Optional.of(classValue);
        }

        return Optional.empty();
    }

    /**
     * @return All declared classes
     */
    Set<ClassValue> getClasses();
}