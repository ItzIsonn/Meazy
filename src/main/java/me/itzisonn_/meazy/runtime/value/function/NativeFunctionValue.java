package me.itzisonn_.meazy.runtime.value.function;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * Represents native function value
 */
@EqualsAndHashCode(callSuper = true)
public abstract class NativeFunctionValue extends FunctionValue {
    /**
     * @param id Id
     * @param args Args
     * @param returnDataType Which DataType should this function return or null
     * @param parentEnvironment Parent environment
     * @param modifiers Modifiers
     */
    public NativeFunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(id, args, returnDataType, parentEnvironment, modifiers);
    }

    /**
     * Runs this function with given args and environment
     *
     * @param functionArgs Args given to this function
     * @param functionEnvironment Unique Environment of this function
     */
    public abstract RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, FunctionEnvironment functionEnvironment);
}
