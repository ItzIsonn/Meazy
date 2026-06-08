package me.itzisonn_.meazy.runtime.environment;

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
    void declareClass(ClassEnvironment classEnvironment);

    /**
     * @param id Class's id
     * @return Declared class with given id or null
     */
    default Optional<ClassEnvironment> getClass(String id) {
        for (ClassEnvironment classEnvironment : getClasses()) {
            if (classEnvironment.getId().equals(id)) return Optional.of(classEnvironment);
        }

        return Optional.empty();
    }

    /**
     * @return All declared classes
     */
    Set<ClassEnvironment> getClasses();
}