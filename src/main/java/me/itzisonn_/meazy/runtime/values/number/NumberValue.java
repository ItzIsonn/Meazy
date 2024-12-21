package me.itzisonn_.meazy.runtime.values.number;

import me.itzisonn_.meazy.runtime.values.RuntimeValue;

public abstract class NumberValue<T extends Number> extends RuntimeValue<T> {
    public NumberValue(T value) {
        super(value);
    }
}