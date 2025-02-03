package me.itzisonn_.meazy.runtime.values;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class VariableValue extends RuntimeValue<RuntimeValue<?>> {
    private final String id;
    private final DataType dataType;
    private RuntimeValue<?> value;
    private final boolean isConstant;
    private final Set<String> accessModifiers;
    private final boolean isArgument;
    private final Environment parentEnvironment;

    public VariableValue(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers, boolean isArgument, Environment parentEnvironment) {
        super(null);
        this.id = id;
        this.dataType = dataType;
        setValue(value);
        this.isConstant = isConstant;
        this.accessModifiers = accessModifiers;
        this.isArgument = isArgument;
        this.parentEnvironment = parentEnvironment;
    }

    public void setValue(RuntimeValue<?> value) {
        if (isConstant && this.value != null && this.value.getFinalValue() != null) {
            throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);
        }

        if (!dataType.isMatches(value)) throw new InvalidSyntaxException("Variable with id " + id + " requires data type " + dataType.getId());
        this.value = value;
    }
}