package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents runtime class value created directly in code
 */
@EqualsAndHashCode(callSuper = true)
public class DefaultClassValue extends ClassValue {
    /**
     * DefaultClassValue constructor
     *
     * @param baseClasses DefaultClassValue's base classes
     * @param environment DefaultClassValue's environment
     *
     * @throws NullPointerException If either baseClasses or environment is null
     */
    public DefaultClassValue(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(baseClasses, environment);
    }

    /**
     * DefaultClassValue constructor with empty baseClasses
     * @param environment DefaultClassValue's environment
     * @throws NullPointerException If given environment is null
     */
    public DefaultClassValue(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }
}