package me.itzisonn_.meazy.runtime.value.classes.constructors;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * Represents runtime constructor value created directly in code
 */
public abstract class DefaultConstructorValue extends ConstructorValue {
    /**
     * DefaultConstructorValue constructor
     *
     * @param args DefaultConstructorValue's args
     * @param parentEnvironment DefaultConstructorValue's parent environment
     * @param modifiers DefaultConstructorValue's modifiers
     *
     * @throws NullPointerException If either args, parentEnvironment or modifiers is null
     */
    public DefaultConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(args, parentEnvironment, modifiers);
    }

    /**
     * Runs this DefaultConstructorValue with given args and environment
     *
     * @param constructorArgs Args given to this DefaultConstructorValue
     * @param constructorEnvironment Unique Environment of this DefaultConstructorValue
     */
    public abstract void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment);

    @Override
    public final ConstructorValue copy(ConstructorDeclarationEnvironment parentEnvironment) {
        RunFunction runFunction = this::run;

        return new DefaultConstructorValue(args, parentEnvironment, modifiers) {
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