package me.itzisonn_.meazy.runtime.values.statement_info;

import me.itzisonn_.meazy.runtime.values.RuntimeValue;

public abstract class StatementInfoValue<T> extends RuntimeValue<T> {
    protected StatementInfoValue(T value) {
        super(value);
    }
}
