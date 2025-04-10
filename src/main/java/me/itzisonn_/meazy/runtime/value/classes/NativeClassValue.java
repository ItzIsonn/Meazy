package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents runtime class value created directly in code
 */
@EqualsAndHashCode(callSuper = true)
public class NativeClassValue extends ClassValue {
    /**
     * Main constructor
     *
     * @param baseClasses Base classes
     * @param environment Environment
     *
     * @throws NullPointerException If either baseClasses or environment is null
     */
    public NativeClassValue(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(baseClasses, environment);
    }

    /**
     * Constructor with empty baseClasses
     *
     * @param environment Environment
     * @throws NullPointerException If given environment is null
     */
    public NativeClassValue(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }
}