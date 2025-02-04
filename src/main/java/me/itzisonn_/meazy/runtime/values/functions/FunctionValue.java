package me.itzisonn_.meazy.runtime.values.functions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.Modifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * FunctionValue represents runtime function value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class FunctionValue extends RuntimeValue<Object> {
    protected final String id;
    protected final List<CallArgExpression> args;
    protected final DataType returnDataType;
    protected final FunctionDeclarationEnvironment parentEnvironment;
    protected final Set<Modifier> modifiers;

    /**
     * FunctionValue constructor
     *
     * @param id Id of this FunctionValue
     * @param args Args of this FunctionValue
     * @param returnDataType Which DataType should this FunctionValue return
     * @param parentEnvironment Parent of this FunctionValue
     * @param modifiers AccessModifiers of this FunctionValue
     */
    public FunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<Modifier> modifiers) {
        super(null);
        this.id = id;
        this.args = args;
        this.returnDataType = returnDataType;
        this.parentEnvironment = parentEnvironment;
        this.modifiers = modifiers;
    }
}
