package me.itzisonn_.meazy.parser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.value.NullValue;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.classes.ClassValue;

/**
 * Defines which values can be stored in variables, args, etc.
 */
@Getter
@EqualsAndHashCode
public class DataType {
    /**
     * DataType's id
     */
    private final String id;
    /**
     * Whether this DataType accepts {@link NullValue}
     */
    private final boolean isNullable;

    /**
     * DataType constructor
     *
     * @param id DataType's id
     * @param isNullable Whether this DataType accepts {@link NullValue}
     *
     * @throws NullPointerException If given id is null
     */
    public DataType(String id, boolean isNullable) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        this.id = id;
        this.isNullable = isNullable;
    }

    /**
     * Checks whether given value matches this DataType
     *
     * @param value Value to check
     * @param globalEnvironment GlobalEnvironment that contains class with this DataType's name
     * @return Whether given value matches this DataType
     *
     * @throws NullPointerException If given globalEnvironment is null
     */
    public boolean isMatches(RuntimeValue<?> value, GlobalEnvironment globalEnvironment) throws NullPointerException {
        if (globalEnvironment == null) throw new NullPointerException("GlobalEnvironment can't be null");
        if (value == null) return true;

        value = value.getFinalRuntimeValue();
        if (value instanceof NullValue) return isNullable;

        ClassValue classValue = globalEnvironment.getClass(id);
        return classValue != null && classValue.isLikeMatches(value);
    }

    @Override
    public String toString() {
        return id + (isNullable ? "?" : "");
    }
}