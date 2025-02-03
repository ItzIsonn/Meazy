package me.itzisonn_.meazy.parser.ast;

import lombok.Getter;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.runtime.values.NullValue;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;
import me.itzisonn_.meazy.runtime.values.classes.ClassValue;

/**
 * DataType
 */
@Getter
public class DataType {
    /**
     * DataType's id
     */
    private final String id;
    /**
     * Whether this DataType accepts null value
     */
    private final boolean isNullable;

    /**
     * DataType constructor
     *
     * @param id DataType's id
     * @param isNullable Whether this DataType accepts null value
     */
    public DataType(String id, boolean isNullable) {
        this.id = id;
        this.isNullable = isNullable;
    }

    /**
     * @param value Value to check
     * @return Whether given value matches this DataType
     */
    public boolean isMatches(RuntimeValue<?> value) {
        if (value == null) return true;

        value = value.getFinalRuntimeValue();
        if (value instanceof NullValue) return isNullable;

        ClassValue classValue = Registries.GLOBAL_ENVIRONMENT.getEntry().getValue().getClass(id);
        return classValue != null && classValue.isMatches(value);
    }

    @Override
    public String toString() {
        return id + (isNullable ? "?" : "");
    }
}