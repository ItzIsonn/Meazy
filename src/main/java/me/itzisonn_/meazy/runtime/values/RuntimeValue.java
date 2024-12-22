package me.itzisonn_.meazy.runtime.values;

import lombok.Getter;

@Getter
public abstract class RuntimeValue<T> {
    protected final T value;

    protected RuntimeValue(T value) {
        this.value = value;
    }

    public final Object getFinalValue() {
        Object value = getValue();
        while (value instanceof RuntimeValue<?> runtimeValue) {
            value = runtimeValue.getValue();
        }
        return value;
    }

    public final RuntimeValue<?> getFinalRuntimeValue() {
        RuntimeValue<?> value = this;
        while (value.getValue() instanceof RuntimeValue<?> runtimeValue) {
            value = runtimeValue;
        }
        return value;
    }
}
