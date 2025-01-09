package me.itzisonn_.meazy.runtime.environment.basic;

import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BasicVariableDeclarationEnvironment extends BasicEnvironment implements VariableDeclarationEnvironment {
    protected final List<RuntimeVariable> variables;

    public BasicVariableDeclarationEnvironment(Environment parent, boolean isShared) {
        super(parent, isShared);
        this.variables = new ArrayList<>();
    }

    public BasicVariableDeclarationEnvironment(Environment parent) {
        super(parent);
        this.variables = new ArrayList<>();
    }

    @Override
    public void declareVariable(String id, RuntimeValue<?> arraySize, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers) {
        RuntimeVariable runtimeVariable = getVariable(id);
        if (runtimeVariable != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(new RuntimeVariable(id, arraySize, dataType, value, isConstant, accessModifiers, false));
    }

    @Override
    public void declareArgument(String id, RuntimeValue<?> arraySize, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers) {
        RuntimeVariable runtimeVariable = getVariable(id);
        if (runtimeVariable != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(new RuntimeVariable(id, arraySize, dataType, value, isConstant, accessModifiers, true));
    }

    @Override
    public List<RuntimeVariable> getVariables() {
        return new ArrayList<>(variables);
    }
}