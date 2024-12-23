package me.itzisonn_.meazy.runtime.values.function;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.Set;

@Getter
public class FunctionValue extends RuntimeValue<Object> {
    private final ArrayList<CallArgExpression> args;
    private final ArrayList<Statement> body;
    private final DataType returnDataType;
    private final FunctionDeclarationEnvironment parentEnvironment;
    private final Set<AccessModifier> accessModifiers;

    public FunctionValue(ArrayList<CallArgExpression> args, ArrayList<Statement> body, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.args = args;
        this.body = body;
        this.returnDataType = returnDataType;
        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }
}
