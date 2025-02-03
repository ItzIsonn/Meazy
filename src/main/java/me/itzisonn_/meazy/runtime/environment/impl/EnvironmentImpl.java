package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.VariableValue;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentImpl implements Environment {
    @Getter
    protected final Environment parent;
    protected boolean isShared;
    protected final List<VariableValue> variables;

    public EnvironmentImpl(Environment parent, boolean isShared) {
        this.parent = parent;
        this.isShared = isShared;
        variables = new ArrayList<>();
    }

    public EnvironmentImpl(Environment parent) {
        this(parent, false);
    }

    public boolean isShared() {
        if (isShared) return true;
        if (parent != null) return parent.isShared();
        return false;
    }

    @Override
    public void declareVariable(VariableValue value) {
        if (value.isArgument()) {
            if (getVariable(value.getId()) != null) {
                throw new InvalidSyntaxException("Variable with id " + value.getId() + " already exists!");
            }
        }
        else if (getVariableDeclarationEnvironment(value.getId()) != null) {
            throw new InvalidSyntaxException("Variable with id " + value.getId() + " already exists!");
        }
        variables.add(value);
    }

    @Override
    public List<VariableValue> getVariables() {
        return new ArrayList<>(variables);
    }
}