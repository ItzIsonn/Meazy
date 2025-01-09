package me.itzisonn_.meazy.runtime.values.clazz.constructor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class ConstructorValue extends RuntimeValue<Object> {
    protected final List<CallArgExpression> args;
    protected final ConstructorDeclarationEnvironment parentEnvironment;
    protected final Set<AccessModifier> accessModifiers;

    public ConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.args = args;
        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }
}
