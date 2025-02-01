package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VariableDeclarationEnvironmentImpl extends EnvironmentImpl implements VariableDeclarationEnvironment {
    protected final List<RuntimeVariable> variables;

    public VariableDeclarationEnvironmentImpl(Environment parent, boolean isShared) {
        super(parent, isShared);
        this.variables = new ArrayList<>();
    }

    public VariableDeclarationEnvironmentImpl(Environment parent) {
        super(parent);
        this.variables = new ArrayList<>();
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