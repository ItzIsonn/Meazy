package me.itzisonn_.meazy.runtime.values.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

/**
 * ClassValue represents runtime class value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValue extends RuntimeValue<Object> {
    protected final ClassEnvironment classEnvironment;
    protected final String id;

    /**
     * ClassValue constructor
     * @param classEnvironment ClassEnvironment of this ClassValue
     */
    public ClassValue(ClassEnvironment classEnvironment) {
        super(null);
        this.classEnvironment = classEnvironment;
        this.id = classEnvironment.getId();
    }

    @Override
    public String toString() {
        return "Class(" + id + ")";
    }
}
