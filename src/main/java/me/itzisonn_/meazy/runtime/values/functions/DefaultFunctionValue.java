package me.itzisonn_.meazy.runtime.values.functions;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

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
     * @param accessModifiers AccessModifiers of this DefaultFunctionValue
     */
    public DefaultFunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(id, args, returnDataType, parentEnvironment, accessModifiers);
    }

    /**
     * Runs this DefaultFunctionValue with given args and environment
     *
     * @param functionArgs Args given to this DefaultFunctionValue
     * @param functionEnvironment Unique Environment of this DefaultFunctionValue
     */
    public abstract RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment);
}
