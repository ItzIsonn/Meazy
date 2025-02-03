package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EnvironmentImpl implements Environment {
    @Getter
    protected final Environment parent;
    protected boolean isShared;
    protected final List<RuntimeVariable> variables;

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
    public void declareVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers) {
        if (getVariableDeclarationEnvironment(id) != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(new RuntimeVariable(id, dataType, value, isConstant, accessModifiers, false));
    }

    @Override
    public void declareArgument(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers) {
        if (getVariable(id) != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(new RuntimeVariable(id, dataType, value, isConstant, accessModifiers, true));
    }

    @Override
    public List<RuntimeVariable> getVariables() {
        return new ArrayList<>(variables);
    }
}