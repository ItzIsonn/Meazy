package me.itzisonn_.meazy.runtime.value.function;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * DefaultFunctionValue represents runtime function value created directly in code
 */
@EqualsAndHashCode(callSuper = true)
public abstract class DefaultFunctionValue extends FunctionValue {
    /**
     * DefaultFunctionValue constructor
     *
     * @param id Id of this DefaultFunctionValue
     * @param args Args of this DefaultFunctionValue
     * @param returnDataType Which DataType should this DefaultFunctionValue return
     * @param parentEnvironment Parent of this DefaultFunctionValue
     * @param modifiers Modifiers of this DefaultFunctionValue
     */
    public DefaultFunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(id, args, returnDataType, parentEnvironment, modifiers);
    }

    /**
     * Runs this DefaultFunctionValue with given args and environment
     *
     * @param functionArgs Args given to this DefaultFunctionValue
     * @param functionEnvironment Unique Environment of this DefaultFunctionValue
     */
    public abstract RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment);

    /**
     * Copies this DefaultFunctionValue with given parent environment
     *
     * @param parentEnvironment New parent of this DefaultFunctionValue
     * @return Copy of this DefaultFunctionValue
     */
    @Override
    public final FunctionValue copy(FunctionDeclarationEnvironment parentEnvironment) {
        RunFunction runFunction = this::run;

        return new DefaultFunctionValue(id, args, returnDataType, parentEnvironment, modifiers) {
            @Override
            public RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment) {
                return runFunction.run(functionArgs, functionEnvironment);
            }
        };
    }

    private interface RunFunction {
        RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment);
    }
}
