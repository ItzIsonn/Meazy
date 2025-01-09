package me.itzisonn_.meazy.runtime.values;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * RuntimeValue is used to represent values in runtime
 *
 * @param <T> Type of stored value
 */
@Getter
@EqualsAndHashCode
public abstract class RuntimeValue<T> {
    /**
     * Value
     */
    protected final T value;

    /**
     * RuntimeValue constructor
     *
     * @param value Value to store
     */
    protected RuntimeValue(T value) {
        this.value = value;
    }

    /**
     * While {@link RuntimeValue#value} represents {@link RuntimeValue}, it uses {@link RuntimeValue#getValue()} on it. When then loop ends, returns resulting value
     *
     * @return Final value
     */
    public final Object getFinalValue() {
        Object value = getValue();
        while (value instanceof RuntimeValue<?> runtimeValue) {
            value = runtimeValue.getValue();
        }
        return value;
    }


    /**
     * While {@link RuntimeValue#value#getValue()} represents {@link RuntimeValue}, it uses {@link RuntimeValue#getValue()} on it. When then loop ends, returns resulting value
     *
     * @return Final RuntimeValue
     */
    public final RuntimeValue<?> getFinalRuntimeValue() {
        RuntimeValue<?> value = this;
        while (value.getValue() instanceof RuntimeValue<?> runtimeValue) {
            value = runtimeValue;
        }
        return value;
    }
}
