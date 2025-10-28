package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.Environment;

import java.util.List;
import java.util.Set;

/**
 * Represents constructor value
 */
public interface ConstructorValue extends ModifierableRuntimeValue<Object> {
    /**
     * @return Parameters
     */
    List<ParameterExpression> getParameters();

    /**
     * @return Parent environment
     */
    ConstructorDeclarationEnvironment getParentEnvironment();



    /**
     * Runs this constructor with given args and environment
     *
     * @param context Runtime context
     * @param constructorEnvironment Unique Environment of this constructor
     * @param callEnvironment Environment from which this constructor is called
     * @param args Args given to this constructor
     *
     * @return Ids of all called base classes
     */
    Set<String> run(RuntimeContext context, ConstructorEnvironment constructorEnvironment, Environment callEnvironment, List<RuntimeValue<?>> args);
}
