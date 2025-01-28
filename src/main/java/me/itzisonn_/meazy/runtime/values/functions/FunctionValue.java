package me.itzisonn_.meazy.runtime.values.functions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
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
    protected final String returnDataType;
    @Setter
    protected FunctionDeclarationEnvironment parentEnvironment;
    protected final Set<AccessModifier> accessModifiers;

    /**
     * FunctionValue constructor
     *
     * @param id Id of this FunctionValue
     * @param args Args of this FunctionValue
     * @param returnDataType Which datatype should this FunctionValue return
     * @param parentEnvironment Parent of this FunctionValue
     * @param accessModifiers AccessModifiers of this FunctionValue
     */
    public FunctionValue(String id, List<CallArgExpression> args, String returnDataType, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.id = id;
        this.args = args;
        this.returnDataType = returnDataType;
        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }
}
