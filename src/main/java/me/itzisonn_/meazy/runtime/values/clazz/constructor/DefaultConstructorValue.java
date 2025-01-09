package me.itzisonn_.meazy.runtime.values.clazz.constructor;

import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

public abstract class DefaultConstructorValue extends ConstructorValue {
    public DefaultConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(args, parentEnvironment, accessModifiers);
    }

    public abstract void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment);

    public final DefaultConstructorValue copy(ConstructorDeclarationEnvironment parentEnvironment) {
        RunFunction runFunction = this::run;

        return new DefaultConstructorValue(args, parentEnvironment, accessModifiers) {
            @Override
            public void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment) {
                runFunction.run(constructorArgs, constructorEnvironment);
            }
        };
    }

    private interface RunFunction {
        void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment);
    }
}