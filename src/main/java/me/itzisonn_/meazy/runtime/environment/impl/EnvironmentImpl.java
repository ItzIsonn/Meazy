package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.runtime.environment.Environment;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class EnvironmentImpl implements Environment {
    private final Environment parent;

    public EnvironmentImpl(Environment parent) {
        this.parent = parent;
    }

    @Override
    public Environment getParent() {
        return this.parent;
    }
}