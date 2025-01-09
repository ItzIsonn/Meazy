package me.itzisonn_.meazy.runtime.values.function;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class FunctionValue extends RuntimeValue<Object> {
    protected final String id;
    protected final List<CallArgExpression> args;
    protected final DataType returnDataType;
    protected final IntValue arraySize;
    @Setter
    protected FunctionDeclarationEnvironment parentEnvironment;
    protected final Set<AccessModifier> accessModifiers;

    public FunctionValue(String id, List<CallArgExpression> args, DataType returnDataType, RuntimeValue<?> arraySize, FunctionDeclarationEnvironment parentEnvironment, Set<AccessModifier> accessModifiers) {
        super(null);
        this.id = id;
        this.args = args;
        this.returnDataType = returnDataType;

        switch (arraySize) {
            case IntValue intValue -> {
                if (intValue.getValue() < 0) throw new InvalidSyntaxException("Array size must be positive value");
                this.arraySize = intValue;
            }
            case NullValue ignored -> this.arraySize = new IntValue(-1);
            case null -> this.arraySize = null;
            default -> throw new InvalidSyntaxException("Array size must be int value");
        }

        this.parentEnvironment = parentEnvironment;
        this.accessModifiers = accessModifiers;
    }
}
