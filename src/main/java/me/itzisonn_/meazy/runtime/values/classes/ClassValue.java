package me.itzisonn_.meazy.runtime.values.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

/**
 * ClassValue represents runtime class value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValue extends RuntimeValue<Object> {
    protected final ClassEnvironment environment;

    /**
     * ClassValue constructor
     * @param environment ClassEnvironment of this ClassValue
     */
    public ClassValue(ClassEnvironment environment) {
        super(null);
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
     * @return Id of this ClassValue's environment
     */
    public String getId() {
        return environment.getId();
    }

    @Override
    public String toString() {
        return "Class(" + getId() + ")";
    }
}
