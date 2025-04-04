package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents runtime class value created at runtime
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeClassValue extends ClassValue {
    /**
     * RuntimeClassValue's body
     */
    private final List<Statement> body;

    /**
     * RuntimeClassValue constructor
     *
     * @param baseClasses RuntimeClassValue's base classes
     * @param environment RuntimeClassValue's environment
     * @param body RuntimeClassValue's body
     *
     * @throws NullPointerException If either baseClasses, environment or body is null
     */
    public RuntimeClassValue(Set<String> baseClasses, ClassEnvironment environment, List<Statement> body) {
        super(baseClasses, environment);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;
    }

    /**
     * RuntimeClassValue constructor with empty baseClasses
     *
     * @param environment RuntimeClassValue's environment
     * @param body RuntimeClassValue's body
     *
     * @throws NullPointerException If either environment or body is null
     */
    public RuntimeClassValue(ClassEnvironment environment, List<Statement> body) {
        this(new HashSet<>(), environment, body);
    }
}
