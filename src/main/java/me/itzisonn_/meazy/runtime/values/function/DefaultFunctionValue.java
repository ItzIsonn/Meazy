package me.itzisonn_.meazy.runtime.values.function;

import lombok.EqualsAndHashCode;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
public abstract class DefaultFunctionValue extends FunctionValue {
    public DefaultFunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, RuntimeValue<?> arraySize, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(id, args, returnDataType, arraySize, parentEnvironment, accessModifiers);
    }

    public DefaultFunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(id, args, returnDataType, null, parentEnvironment, accessModifiers);
    }

    public abstract RuntimeValue<?> run(List<RuntimeValue<?>> functionArgs, Environment functionEnvironment);
}
