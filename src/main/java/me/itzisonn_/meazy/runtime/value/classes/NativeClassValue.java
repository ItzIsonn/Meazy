package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents native class value
 */
@EqualsAndHashCode(callSuper = true)
public class NativeClassValue extends ClassValue {
    /**
     * @param baseClasses Base classes
     * @param environment Class environment
     *
     * @throws NullPointerException If either baseClasses or environment is null
     */
    public NativeClassValue(Set<String> baseClasses, ClassEnvironment environment) throws NullPointerException {
        super(baseClasses, environment);
    }

    /**
     * Constructor with empty baseClasses
     *
     * @param environment Class environment
     * @throws NullPointerException If given environment is null
     */
    public NativeClassValue(ClassEnvironment environment) throws NullPointerException {
        this(new HashSet<>(), environment);
    }

    /**
     * Creates new instance of this class value
     *
     * @param baseClasses Base classes
     * @param classEnvironment Class environment
     * @return New instance of this class value
     */
    public NativeClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment) {
        return new NativeClassValue(baseClasses, classEnvironment);
    }

    /**
     * Setups given environment
     * @param classEnvironment Class environment
     */
    public void setupEnvironment(ClassEnvironment classEnvironment) {}
}