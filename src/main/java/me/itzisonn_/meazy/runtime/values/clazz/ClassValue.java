package me.itzisonn_.meazy.runtime.values.clazz;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.runtime.environment.interfaces.ClassEnvironment;
import me.itzisonn_.meazy.runtime.values.InnerStringValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ClassValue extends RuntimeValue<Object> {
    private final ClassEnvironment classEnvironment;
    private final String id;
    private final ArrayList<Statement> body;

    public ClassValue(ClassEnvironment classEnvironment, ArrayList<Statement> body) {
        super(new InnerStringValue(classEnvironment.getId()));
        this.classEnvironment = classEnvironment;
        this.id = classEnvironment.getId();
        this.body = body;
    }
}
