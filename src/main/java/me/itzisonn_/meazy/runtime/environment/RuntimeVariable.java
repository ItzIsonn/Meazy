package me.itzisonn_.meazy.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.ArrayValue;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.VariableValue;
import me.itzisonn_.meazy.runtime.values.number.IntValue;

import java.util.ArrayList;
import java.util.Set;

@Getter
public class RuntimeVariable {
    private final String id;
    private IntValue arraySize;
    private final DataType dataType;
    private RuntimeValue<?> value;
    private final boolean isConstant;
    private final Set<AccessModifier> accessModifiers;
    private final boolean isArgument;

    public RuntimeVariable(String id, RuntimeValue<?> arraySize, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers, boolean isArgument) {
        this.id = id;

        switch (arraySize) {
            case IntValue intValue -> {
                if (intValue.getValue() < 0) throw new InvalidSyntaxException("Array size must be positive value");
                this.arraySize = intValue;
            }
            case NullValue ignored -> this.arraySize = new IntValue(-1);
            case null -> this.arraySize = null;
            default -> throw new InvalidSyntaxException("Array size must be int value");
        }
        this.dataType = dataType;

        setValue(value);

        this.isConstant = isConstant;
        this.accessModifiers = accessModifiers;
        this.isArgument = isArgument;
    }

    public void setValue(RuntimeValue<?> value) {
        if (isConstant && this.value != null && this.value.getFinalValue() != null)
            throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);

        if (value.getFinalRuntimeValue() instanceof NullValue && value instanceof VariableValue variableValue && arraySize != null) {
            ArrayList<RuntimeValue<?>> emptyList = new ArrayList<>();
            for (int i = 0; i < arraySize.getValue(); i++) {
                emptyList.add(new NullValue());
            }
            this.value = new VariableValue(new ArrayValue(emptyList), variableValue.getParentEnvironment(), variableValue.getId());
            return;
        }

        checkValue(value);
        this.value = value;
    }

    private void checkValue(RuntimeValue<?> value) {
        if (value == null) return;

        if (arraySize != null) {
            if (!(value.getFinalRuntimeValue() instanceof ArrayValue arrayValue)) throw new InvalidSyntaxException("Variable with id " + id + " requires array value");

            if (arraySize.getValue() == -1) arraySize = new IntValue(arrayValue.getValue().size());
            if (arraySize.getValue() != arrayValue.getValue().size()) throw new InvalidSyntaxException("Array size of variable with id " + id + " doesn't match with assigned value");

            for (RuntimeValue<?> runtimeValue : arrayValue.getValue()) {
                if (!dataType.isMatches(runtimeValue.getFinalRuntimeValue()))
                    throw new InvalidSyntaxException("Variable with id " + id + " requires data type " + dataType.getName());
            }
            return;
        }
        else if (value instanceof ArrayValue) throw new InvalidSyntaxException("Variable with id " + id + " can't have array value");

        if (!dataType.isMatches(value.getFinalRuntimeValue()))
            throw new InvalidSyntaxException("Variable with id " + id + " requires data type " + dataType.getName());
    }
}