package me.itzisonn_.meazy.runtime.values.classes;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;

/**
 * DefaultClassValue represents runtime class value created directly in code
 */
@EqualsAndHashCode(callSuper = true)
public class DefaultClassValue extends ClassValue {
    /**
     * DefaultClassValue constructor
     * @param classEnvironment ClassEnvironment of this DefaultClassValue
     */
    public DefaultClassValue(ClassEnvironment classEnvironment) {
        super(classEnvironment);
    }
}