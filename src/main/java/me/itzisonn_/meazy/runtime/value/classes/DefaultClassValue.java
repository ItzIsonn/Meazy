package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.Set;

/**
 * DefaultClassValue represents runtime class value created directly in code
 */
@EqualsAndHashCode(callSuper = true)
public class DefaultClassValue extends ClassValue {
    /**
     * DefaultClassValue constructor
     *
     * @param baseClasses Base classes of this DefaultClassValue
     * @param classEnvironment ClassEnvironment of this DefaultClassValue
     */
    public DefaultClassValue(Set<String> baseClasses, ClassEnvironment classEnvironment) {
        super(baseClasses, classEnvironment);
    }

    /**
     * DefaultClassValue constructor with empty baseClasses
     * @param classEnvironment ClassEnvironment of this DefaultClassValue
     */
    public DefaultClassValue(ClassEnvironment classEnvironment) {
        this(new HashSet<>(), classEnvironment);
    }
}