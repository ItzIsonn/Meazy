package me.itzisonn_.meazy.runtime.value.function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.parser.ast.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Represents runtime function value created at runtime
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeFunctionValue extends FunctionValue {
    /**
     * RuntimeFunctionValue's body
     */
    private final List<Statement> body;

    /**
     * RuntimeFunctionValue constructor
     *
     * @param id RuntimeFunctionValue' id
     * @param args RuntimeFunctionValue's args
     * @param returnDataType Which DataType should this RuntimeFunctionValue return or null
     * @param parentEnvironment RuntimeFunctionValue's parent environment
     * @param modifiers RuntimeFunctionValue's modifiers
     */
    public RuntimeFunctionValue(String id, List<CallArgExpression> args, List<Statement> body, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(id, args, returnDataType, parentEnvironment, modifiers);
        this.body = body;
    }

    @Override
    public final FunctionValue copy(FunctionDeclarationEnvironment parentEnvironment) {
        return new RuntimeFunctionValue(id, args, new ArrayList<>(body), returnDataType, parentEnvironment, modifiers);
    }
}
