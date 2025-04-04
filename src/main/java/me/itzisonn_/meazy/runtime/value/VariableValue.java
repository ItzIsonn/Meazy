package me.itzisonn_.meazy.runtime.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;

import java.util.Set;

/**
 * Represents runtime variable value
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class VariableValue extends RuntimeValue<RuntimeValue<?>> {
    /**
     * VariableValue's id
     */
    private final String id;
    /**
     * VariableValue's DataType
     */
    private final DataType dataType;
    /**
     * VariableValue's value
     */
    private RuntimeValue<?> value;
    /**
     * Whether this VariableValue's value is constant
     */
    private final boolean isConstant;
    /**
     * VariableValue's modifiers
     */
    private final Set<Modifier> modifiers;
    /**
     * Whether this VariableValue is argument
     */
    private final boolean isArgument;

    /**
     * VariableValue constructor
     *
     * @param id VariableValue's id
     * @param dataType VariableValue's DataType
     * @param value VariableValue's value
     * @param isConstant Whether this VariableValue's value is constant
     * @param modifiers VariableValue's modifiers
     * @param isArgument Whether this VariableValue is argument
     *
     * @throws NullPointerException If either id, dataType or modifiers is null
     */
    public VariableValue(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<Modifier> modifiers, boolean isArgument) throws NullPointerException {
        super(null);

        if (id == null) throw new NullPointerException("Id can't be null");
        if (dataType == null) throw new NullPointerException("DataType can't be null");
        if (modifiers == null) throw new NullPointerException("Modifiers can't be null");

        this.id = id;
        this.dataType = dataType;
        setValue(value);
        this.isConstant = isConstant;
        this.modifiers = modifiers;
        this.isArgument = isArgument;
    }

    /**
     * Sets value of this VariableValue to given value
     *
     * @param value New value
     *
     * @throws InvalidSyntaxException If this VariableValue is constant and already have a value
     * @throws InvalidValueException If given value doesn't match this VariableValue's {@link VariableValue#dataType}
     */
    public void setValue(RuntimeValue<?> value) throws InvalidSyntaxException, InvalidValueException {
        if (isConstant && this.value != null && this.value.getFinalValue() != null) {
            throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);
        }

        if (!dataType.isMatches(value)) throw new InvalidValueException("Variable with id " + id + " requires data type " + dataType.getId());
        this.value = value;
    }
}