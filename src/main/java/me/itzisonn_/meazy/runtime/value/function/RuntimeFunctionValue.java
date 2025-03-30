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
 * RuntimeFunctionValue represents runtime function value created at runtime
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class RuntimeFunctionValue extends FunctionValue {
    private final List<Statement> body;

    /**
     * RuntimeFunctionValue constructor
     *
     * @param id Id of this RuntimeFunctionValue
     * @param args Args of this RuntimeFunctionValue
     * @param returnDataType Which DataType should this RuntimeFunctionValue return
     * @param parentEnvironment Parent of this RuntimeFunctionValue
     * @param modifiers Modifiers of this RuntimeFunctionValue
     * @param body Body of this RuntimeFunctionValue
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
