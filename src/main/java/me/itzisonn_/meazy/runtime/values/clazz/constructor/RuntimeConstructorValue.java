package me.itzisonn_.meazy.runtime.values.clazz.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeConstructorValue extends ConstructorValue {
    private final List<Statement> body;

    public RuntimeConstructorValue(List<CallArgExpression> args, List<Statement> body, ConstructorDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(args, parentEnvironment, accessModifiers);
        this.body = body;
    }
}
