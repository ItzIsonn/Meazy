package me.itzisonn_.meazy.parser.data_type;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import org.jspecify.annotations.NullMarked;

import java.lang.constant.ClassDesc;

/**
 * Defines which values can be stored in variables, args, etc.
 */
@NullMarked
public interface DataType {
    /**
     * @return Class id
     */
    String getId();

    /**
     * @return Whether this data type accepts null values
     */
    boolean isNullable();

    //TODO
    ClassDesc getClassDescriptor(Environment environment);

    DataType withFullClassName(Environment environment);

    /**
     * Checks whether given value matches this data type
     *
     * @param value Value to check
     * @param fileEnvironment File environment that contains class with this data type's name
     * @return Whether given value matches this data type
     */
    boolean isMatches(RuntimeValue value, FileEnvironment fileEnvironment);
}