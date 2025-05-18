package me.itzisonn_.meazy.runtime.value.classes;

import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.Set;

/**
 * Represents native class value
 */
public interface NativeClassValue extends ClassValue {
    /**
     * Creates new instance of this class value
     *
     * @param baseClasses Base classes
     * @param classEnvironment Class environment
     * @return New instance of this class value
     */
    NativeClassValue newInstance(Set<String> baseClasses, ClassEnvironment classEnvironment);

    /**
     * Setups given environment
     * @param classEnvironment Class environment
     */
    void setupEnvironment(ClassEnvironment classEnvironment);
}