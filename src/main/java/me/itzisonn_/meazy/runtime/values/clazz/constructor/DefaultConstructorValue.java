package me.itzisonn_.meazy.runtime.values.clazz.constructor;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.Set;

@Getter
public abstract class DefaultConstructorValue extends RuntimeValue<Object> {
    private final ArrayList<CallArgExpression> args;
    @Setter
    private ConstructorDeclarationEnvironment parentEnvironment;
    private final Set<AccessModifier> accessModifiers;

    public DefaultConstructorValue(ArrayList<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.args = args;
        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }

    public abstract void run(ArrayList<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment);
}