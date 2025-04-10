package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents runtime class value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeClassValue extends ClassValue {
    /**
     * Body
     */
    private final List<Statement> body;

    /**
     * Main constructor
     *
     * @param baseClasses Base classes
     * @param environment Environment
     * @param body Body
     *
     * @throws NullPointerException If either baseClasses, environment or body is null
     */
    public RuntimeClassValue(Set<String> baseClasses, ClassEnvironment environment, List<Statement> body) throws NullPointerException {
        super(baseClasses, environment);

        if (body == null) throw new NullPointerException("Body can't be null");
        this.body = body;
    }

    /**
     * Constructor with empty baseClasses
     *
     * @param environment Environment
     * @param body Body
     *
     * @throws NullPointerException If either environment or body is null
     */
    public RuntimeClassValue(ClassEnvironment environment, List<Statement> body) throws NullPointerException {
        this(new HashSet<>(), environment, body);
    }
}
