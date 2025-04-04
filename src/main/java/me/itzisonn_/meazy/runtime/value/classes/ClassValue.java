package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents runtime class value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValue extends RuntimeValue<Object> {
    /**
     * ClassValue's base classes
     */
    protected final Set<String> baseClasses;
    /**
     * ClassValue's environment
     */
    protected final ClassEnvironment environment;

    /**
     * ClassValue constructor
     *
     * @param baseClasses ClassValue's base classes
     * @param environment ClassValue's environment
     *
     * @throws NullPointerException If either baseClasses or environment is null
     */
    public ClassValue(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(null);

        if (baseClasses == null) throw new NullPointerException("BaseClasses can't be null");
        if (environment == null) throw new NullPointerException("Environment can't be null");

        this.baseClasses = baseClasses;
        this.environment = environment;
    }

    /**
     * ClassValue constructor with empty baseClasses
     * @param environment ClassValue's environment
     * @throws NullPointerException If given environment is null
     */
    public ClassValue(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }

    /**
     * @param value Value to check
     * @return Whether given value matches this ClassValue
     */
    public boolean isMatches(Object value) {
        if (value instanceof ClassValue classValue) return classValue.getId().equals(getId());
        return false;
    }

    /**
     * @param value Value to check
     * @return Whether given value matches this ClassValue or it's base classes
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
