package me.itzisonn_.meazy.runtime.values;

import java.util.List;

public class ArrayValue extends RuntimeValue<List<RuntimeValue<?>>> {
    public ArrayValue(List<RuntimeValue<?>> value) {
        super(value);
    }
}