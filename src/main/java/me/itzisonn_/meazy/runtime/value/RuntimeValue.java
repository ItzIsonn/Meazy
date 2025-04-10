package me.itzisonn_.meazy.runtime.value;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Is used to represent values in runtime
 * @param <T> Type of stored value
 */
@Getter
@EqualsAndHashCode
public class RuntimeValue<T> {
    /**
     * Value
     */
    private final T value;

    /**
     * @param value Value to store
     */
    public RuntimeValue(T value) {
        this.value = value;
    }

    /**
     * Searches for not-{@link RuntimeValue} value through this {@link RuntimeValue} and it's values
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
     * Searches for final {@link RuntimeValue} value through this {@link RuntimeValue} and it's values
     * @return Final RuntimeValue
     */
    public final RuntimeValue<?> getFinalRuntimeValue() {
        RuntimeValue<?> value = this;
        while (value.getValue() instanceof RuntimeValue<?> runtimeValue) {
            value = runtimeValue;
        }
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(getValue());
    }
}
