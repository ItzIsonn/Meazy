package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.Modifier;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.InvalidValueException;

import java.util.Set;

/**
 * Represents runtime variable value
 */
public interface VariableValue extends RuntimeValue<RuntimeValue<?>> {
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
     * @return Modifiers
     */
    Set<Modifier> getModifiers();

    /**
     * @return Whether this variable is argument
     */
    boolean isArgument();

    /**
     * @return Parent Environment
     */
    Environment getParentEnvironment();

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