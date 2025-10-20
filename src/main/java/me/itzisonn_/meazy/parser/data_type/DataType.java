package me.itzisonn_.meazy.parser.data_type;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;

/**
 * Defines which values can be stored in variables, args, etc.
 */
public interface DataType {
    /**
     * @return Id
     */
    String getId();

    /**
     * @return Whether this data type accepts null values
     */
    boolean isNullable();

    /**
     * Checks whether given value matches this data type
     *
     * @param value Value to check
     * @param fileEnvironment File environment that contains class with this data type's name
     * @return Whether given value matches this data type
     *
     * @throws NullPointerException If given globalEnvironment is null
     */
    boolean isMatches(RuntimeValue<?> value, FileEnvironment fileEnvironment) throws NullPointerException;
}