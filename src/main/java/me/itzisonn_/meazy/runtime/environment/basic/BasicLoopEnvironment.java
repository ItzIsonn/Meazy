package me.itzisonn_.meazy.runtime.environment.basic;

import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.LoopEnvironment;

public class BasicLoopEnvironment extends BasicVariableDeclarationEnvironment implements LoopEnvironment {
    public BasicLoopEnvironment(Environment parent, boolean isShared) {
        super(parent, isShared);
    }

    public BasicLoopEnvironment(Environment parent) {
        super(parent);
    }

    @Override
    public void clearVariables() {
        variables.clear();
    }
}