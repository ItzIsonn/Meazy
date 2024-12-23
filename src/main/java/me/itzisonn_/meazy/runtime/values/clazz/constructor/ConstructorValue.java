package me.itzisonn_.meazy.runtime.values.clazz.constructor;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.Set;

@Getter
public class ConstructorValue extends RuntimeValue<Object> {
    private final ArrayList<CallArgExpression> args;
    private final ArrayList<Statement> body;
    private final ConstructorDeclarationEnvironment parentEnvironment;
    private final Set<AccessModifier> accessModifiers;

    public ConstructorValue(ArrayList<CallArgExpression> args, ArrayList<Statement> body, ConstructorDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.args = args;
        this.body = body;
        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }
}
