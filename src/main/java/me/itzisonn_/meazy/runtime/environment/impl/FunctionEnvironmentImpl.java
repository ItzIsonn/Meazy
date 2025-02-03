package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;

public class FunctionEnvironmentImpl extends EnvironmentImpl implements FunctionEnvironment {
    public FunctionEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent, isShared);
    }

    public FunctionEnvironmentImpl(Environment parent) {
        super(parent, false);
    }
}