package me.itzisonn_.meazy.runtime.values.classes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;

import java.util.List;

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
     * @param classEnvironment ClassEnvironment of this RuntimeClassValue
     * @param body Body of this RuntimeClassValue
     */
    public RuntimeClassValue(ClassEnvironment classEnvironment, List<Statement> body) {
        super(classEnvironment);
        this.body = body;
    }
}
