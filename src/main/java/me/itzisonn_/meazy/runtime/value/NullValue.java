package me.itzisonn_.meazy.runtime.value;

/**
 * Represents null value
 */
public class NullValue implements RuntimeValue<Object> {
    @Override
    public Object getValue() {
        return null;
    }
}