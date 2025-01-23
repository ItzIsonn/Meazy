package me.itzisonn_.meazy.runtime.values.clazz;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ClassValue extends RuntimeValue<Object> {
    protected final ClassEnvironment classEnvironment;
    protected final String id;

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
