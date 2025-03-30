package me.itzisonn_.meazy.runtime.value.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.runtime.environment.ClassEnvironment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * RuntimeClassValue represents runtime class value created at runtime
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeClassValue extends ClassValue {
    private final List<Statement> body;

    /**
     * RuntimeClassValue constructor
     *
     * @param baseClasses Base classes of this RuntimeClassValue
     * @param classEnvironment ClassEnvironment of this RuntimeClassValue
     * @param body Body of this RuntimeClassValue
     */
    public RuntimeClassValue(Set<String> baseClasses, ClassEnvironment classEnvironment, List<Statement> body) {
        super(baseClasses, classEnvironment);
        this.body = body;
    }

    /**
     * RuntimeClassValue constructor with empty baseClasses
     *
     * @param classEnvironment ClassEnvironment of this RuntimeClassValue
     * @param body Body of this RuntimeClassValue
     */
    public RuntimeClassValue(ClassEnvironment classEnvironment, List<Statement> body) {
        this(new HashSet<>(), classEnvironment, body);
    }
}
