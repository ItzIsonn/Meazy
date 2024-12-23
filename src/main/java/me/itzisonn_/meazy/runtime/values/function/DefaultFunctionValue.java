package me.itzisonn_.meazy.runtime.values.function;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.Set;

@Getter
public abstract class DefaultFunctionValue extends RuntimeValue<Object> {
    private final ArrayList<CallArgExpression> args;
    private final DataType returnDataType;
    @Setter
    private FunctionDeclarationEnvironment parentEnvironment;
    private final Set<AccessModifier> accessModifiers;

    public DefaultFunctionValue(ArrayList<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.args = args;
        this.returnDataType = returnDataType;
        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }

    public abstract RuntimeValue<?> run(ArrayList<RuntimeValue<?>> functionArgs, Environment functionEnvironment);
}
