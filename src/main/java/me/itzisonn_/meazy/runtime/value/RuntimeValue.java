package me.itzisonn_.meazy.runtime.value;

/**
 * Is used to represent values in runtime
 * @param <T> Type of stored value
 */
public interface RuntimeValue<T> {
    /**
     * @return Stored value
     */
    T getValue();

    /**
     * Searches for not-{@link RuntimeValue} value through this {@link RuntimeValue} and it's values
     * @return Final value
     */
    default Object getFinalValue() {
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
    default RuntimeValue<?> getFinalRuntimeValue() {
        RuntimeValue<?> value = this;
        while (value.getValue() instanceof RuntimeValue<?> runtimeValue) {
            value = runtimeValue;
        }
        return value;
    }
}
