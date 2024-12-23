package me.itzisonn_.meazy.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.Set;

@Getter
public class RuntimeVariable {
    private final String id;
    private final DataType dataType;
    private RuntimeValue<?> value;
    private final boolean isConstant;
    private final Set<AccessModifier> accessModifiers;
    private final boolean isArgument;

    public RuntimeVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers, boolean isArgument) {
        this.id = id;
        this.dataType = dataType;

        if (value != null && value.getFinalValue() != null && !dataType.isMatches(value.getFinalValue()))
            throw new InvalidSyntaxException("Variable with id " + id + " requires data type " + dataType.getName());
        this.value = value;

        this.isConstant = isConstant;
        this.accessModifiers = accessModifiers;
        this.isArgument = isArgument;
    }

    public void setValue(RuntimeValue<?> value) {
        if (isConstant && this.value.getFinalValue() != null) throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);

        if (value.getFinalValue() != null && !dataType.isMatches(value.getFinalValue()))
            throw new InvalidSyntaxException("Variable with id " + id + " requires data type " + dataType.getName());

        this.value = value;
    }
}