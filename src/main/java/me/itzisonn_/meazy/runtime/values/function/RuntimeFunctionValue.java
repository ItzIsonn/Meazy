package me.itzisonn_.meazy.runtime.values.function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeFunctionValue extends FunctionValue {
    private final List<Statement> body;

    public RuntimeFunctionValue(String id, List<CallArgExpression> args, List<Statement> body, DataType returnDataType, RuntimeValue<?> arraySize, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(id, args, returnDataType, arraySize, parentEnvironment, accessModifiers);
        this.body = body;
    }
}
