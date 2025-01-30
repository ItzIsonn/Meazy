package me.itzisonn_.meazy.runtime.values.classes.constructors;

import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * DefaultConstructorValue represents runtime constructor value created directly in code
 */
public abstract class DefaultConstructorValue extends ConstructorValue {
    /**
     * DefaultConstructorValue constructor
     *
     * @param args Args of this DefaultConstructorValue
     * @param parentEnvironment Parent of this DefaultConstructorValue
     * @param accessModifiers Access modifiers of this DefaultConstructorValue
     */
    public DefaultConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<String> accessModifiers) {
        super(args, parentEnvironment, accessModifiers);
    }

    /**
     * Runs this DefaultConstructorValue with given args and environment
     *
     * @param constructorArgs Args given to this DefaultConstructorValue
     * @param constructorEnvironment Unique Environment of this DefaultConstructorValue
     */
    public abstract void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment);

    /**
     * Copies this DefaultConstructorValue with given parent environment
     *
     * @param parentEnvironment New parent of this DefaultConstructorValue
     * @return Copy of this DefaultConstructorValue
     */
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