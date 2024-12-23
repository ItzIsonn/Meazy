package me.itzisonn_.meazy.runtime.environment.interfaces.declaration;

import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.ArrayList;
import java.util.Set;

public interface VariableDeclarationEnvironment extends Environment {
    void declareVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers);
    void declareArgument(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers);
    void assignVariable(String id, RuntimeValue<?> value);
    void clearVariables();
    RuntimeVariable getVariable(String id);
    ArrayList<RuntimeVariable> getVariables();
}