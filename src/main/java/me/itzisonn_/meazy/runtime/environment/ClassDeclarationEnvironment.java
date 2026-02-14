package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.runtime.value.ClassValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    @Nullable
    default ClassValue getClass(String id) {
        for (ClassValue classValue : getClasses()) {
            if (classValue.getId().equals(id)) return classValue;
        }

        return null;
    }

    /**
     * @return All declared classes
     */
    Set<ClassValue> getClasses();



    @Override
    @Nullable
    default ClassDeclarationEnvironment getClassDeclarationEnvironment(String id) {
        ClassValue classValue = getClass(id);
        if (classValue != null) return classValue.getEnvironment().getParent();

        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getClassDeclarationEnvironment(id);
    }
}