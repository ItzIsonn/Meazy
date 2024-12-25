package me.itzisonn_.meazy.runtime.values;

import java.util.ArrayList;

public class ArrayValue extends RuntimeValue<ArrayList<RuntimeValue<?>>> {
    public ArrayValue(ArrayList<RuntimeValue<?>> value) {
        super(value);
    }
}