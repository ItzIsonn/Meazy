package me.itzisonn_.meazy.runtime.values.clazz;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;

@EqualsAndHashCode(callSuper = true)
public class DefaultClassValue extends ClassValue {
    public DefaultClassValue(ClassEnvironment classEnvironment) {
        super(classEnvironment);
    }
}