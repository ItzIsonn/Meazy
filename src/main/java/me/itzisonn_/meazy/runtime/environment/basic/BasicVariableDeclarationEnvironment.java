package me.itzisonn_.meazy.runtime.environment.basic;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.environment.interfaces.declaration.VariableDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
import me.itzisonn_.meazy.runtime.interpreter.UnknownIdentifierException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.Set;

@Getter
public class BasicVariableDeclarationEnvironment extends BasicEnvironment implements VariableDeclarationEnvironment {
    private final ArrayList<RuntimeVariable> variables;

    public BasicVariableDeclarationEnvironment(Environment parent, boolean isShared) {
        super(parent, isShared);
        this.variables = new ArrayList<>();
    }

    public BasicVariableDeclarationEnvironment(Environment parent) {
        super(parent);
        this.variables = new ArrayList<>();
    }

    public void declareVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers) {
        RuntimeVariable runtimeVariable = getVariable(id);
        if (runtimeVariable != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(new RuntimeVariable(id, dataType, value, isConstant, accessModifiers, false));
    }

    public void declareArgument(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers) {
        RuntimeVariable runtimeVariable = getVariable(id);
        if (runtimeVariable != null) throw new InvalidSyntaxException("Variable with id " + id + " already exists!");
        variables.add(new RuntimeVariable(id, dataType, value, isConstant, accessModifiers, true));
    }

    public void assignVariable(String id, RuntimeValue<?> value) {
        getVariable(id).setValue(value);
    }

    public void clearVariables() {
        variables.clear();
    }

    public VariableDeclarationEnvironment getVariableEnvironment(String id) {
        if (getVariable(id) != null) return this;
        if (parent == null || !(parent instanceof VariableDeclarationEnvironment variableDeclarationEnvironment))
            throw new UnknownIdentifierException("Variable with id " + id + " doesn't exist!");
        return variableDeclarationEnvironment.getVariableEnvironment(id);
    }

    public RuntimeVariable getVariable(String id) {
        for (RuntimeVariable runtimeVariable : variables) {
            if (runtimeVariable.getId().equals(id)) return runtimeVariable;
        }

        return null;
    }
}