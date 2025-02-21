package me.itzisonn_.meazy.runtime.values.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.HashSet;
import java.util.Set;

/**
 * ClassValue represents runtime class value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValue extends RuntimeValue<Object> {
    protected final Set<String> baseClasses;
    protected final ClassEnvironment environment;

    /**
     * ClassValue constructor with empty baseClasses
     * @param environment ClassEnvironment of this ClassValue
     */
    public ClassValue(ClassEnvironment environment) {
        this(new HashSet<>(), environment);
    }

    /**
     * ClassValue constructor
     * @param baseClasses Base classes of this ClassValue
     * @param environment ClassEnvironment of this ClassValue
     */
    public ClassValue(Set<String> baseClasses, ClassEnvironment environment) {
        super(null);
        this.baseClasses = baseClasses;
        this.environment = environment;
    }

    /**
     * @param value Value to check
     * @return Whether given value matches this class value
     */
    public boolean isMatches(Object value) {
        if (value instanceof ClassValue classValue) return classValue.getId().equals(getId());
        return false;
    }

    /**
     * @param value Value to check
     * @return Whether given value matches this class value or it's base classes
     */
    public boolean isLikeMatches(Object value) {
        if (isMatches(value)) return true;

        if (value instanceof ClassValue classValue) {
            for (String baseClassString : classValue.getBaseClasses()) {
                ClassValue baseClassValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(baseClassString);
                if (baseClassValue == null) continue;
                if (isLikeMatches(baseClassValue)) return true;
            }
        }
        return false;
    }

    /**
     * @return Id of this ClassValue's environment
     */
    public String getId() {
        return environment.getId();
    }

    /**
     * @return All modifiers of this ClassValue's environment
     */
    public Set<Modifier> getModifiers() {
        return environment.getModifiers();
    }

    @Override
    public String toString() {
        return "Class(" + getId() + ")";
    }
}
