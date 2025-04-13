package me.itzisonn_.meazy.runtime.value.classes.constructor;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * Represents native constructor value
 */
public abstract class NativeConstructorValue extends ConstructorValue {
    /**
     * @param args Args
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     *
     * @throws NullPointerException If either args, parentEnvironment or modifiers is null
     */
    public NativeConstructorValue(List<CallArgExpression> args, ConstructorDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) throws NullPointerException {
        super(args, parentEnvironment, modifiers);
    }

    /**
     * Runs this constructor with given args and environment
     *
     * @param constructorArgs Args given to this constructor
     * @param constructorEnvironment Unique Environment of this constructor
     */
    public abstract void run(List<RuntimeValue<?>> constructorArgs, Environment constructorEnvironment);
}