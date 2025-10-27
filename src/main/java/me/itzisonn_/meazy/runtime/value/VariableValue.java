package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;

/**
 * Represents runtime variable value
 */
public interface VariableValue extends ModifierableRuntimeValue<RuntimeValue<?>> {
    /**
     * @return Id
     */
    String getId();

    /**
     * @return DataType
     */
    DataType getDataType();

    /**
     * @return Whether value is constant
     */
    boolean isConstant();

    /**
     * @return Whether this variable is argument
     */
    boolean isArgument();

    /**
     * @return Parent environment
     */
    VariableDeclarationEnvironment getParentEnvironment();

    /**
     * Sets this variable's value to given value
     *
     * @param value New value
     *
     * @throws InvalidSyntaxException If this variable is constant and already have a value
     * @throws InvalidValueException If given value doesn't match this variable's data type
     */
    void setValue(RuntimeValue<?> value) throws InvalidSyntaxException, InvalidValueException;
}