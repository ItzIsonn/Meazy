package me.itzisonn_.meazy.runtime.values.clazz;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeClassValue extends ClassValue {
    private final List<Statement> body;

    public RuntimeClassValue(ClassEnvironment classEnvironment, List<Statement> body) {
        super(classEnvironment);
        this.body = body;
    }
}
