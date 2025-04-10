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
     * Id
     */
    private final String id;
    /**
     * DataType
     */
    private final DataType dataType;
    /**
     * Value
     */
    private RuntimeValue<?> value;
    /**
     * Whether value is constant
     */
    private final boolean isConstant;
    /**
     * Modifiers
     */
    private final Set<Modifier> modifiers;
    /**
     * Whether this variable is argument
     */
    private final boolean isArgument;

    /**
     * @param id Id
     * @param dataType DataType
     * @param value Value
     * @param isConstant Whether value is constant
     * @param modifiers Modifiers
     * @param isArgument Whether this variable is argument
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
     * Sets this variable's value to given value
     *
     * @param value New value
     *
     * @throws InvalidSyntaxException If this variable is constant and already have a value
     * @throws InvalidValueException If given value doesn't match this variable's {@link VariableValue#dataType}
     */
    public void setValue(RuntimeValue<?> value) throws InvalidSyntaxException, InvalidValueException {
        if (isConstant && this.value != null && this.value.getFinalValue() != null) {
            throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);
        }

        if (!dataType.isMatches(value)) throw new InvalidValueException("Variable with id " + id + " requires data type " + dataType.getId());
        this.value = value;
    }
}