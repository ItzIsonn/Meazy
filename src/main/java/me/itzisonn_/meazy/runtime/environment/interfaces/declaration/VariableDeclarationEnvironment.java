package me.itzisonn_.meazy.runtime.environment.interfaces.declaration;

import me.itzisonn_.meazy.parser.ast.AccessModifier;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.environment.RuntimeVariable;
import me.itzisonn_.meazy.runtime.environment.interfaces.Environment;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.values.RuntimeValue;

import java.util.List;
import java.util.Set;

/**
 * VariableDeclarationEnvironment adds the ability to declare and get variables
 */
public interface VariableDeclarationEnvironment extends Environment {
    /**
     * Declares variable with given data in this environment
     *
     * @param id Variable's id
     * @param arraySize Variable's array size
     * @param dataType Variable's DataType
     * @param value Variable's value
     * @param isConstant Whether is this variable constant
     * @param accessModifiers Variable's AccessModifiers
     */
    void declareVariable(String id, RuntimeValue<?> arraySize, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers);

    /**
     * Declares argument with given data in this environment
     *
     * @param id Argument's id
     * @param arraySize Argument's array size
     * @param dataType Argument's DataType
     * @param value Argument's value
     * @param isConstant Whether is this argument constant
     * @param accessModifiers Argument's AccessModifiers
     */
    void declareArgument(String id, RuntimeValue<?> arraySize, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<AccessModifier> accessModifiers);

    /**
     * Assigns value to existing non-constant variable
     *
     * @param id Variable's id
     * @param value Variable's new value
     */
    default void assignVariable(String id, RuntimeValue<?> value) {
        getVariable(id).setValue(value);
    }

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     */
    default RuntimeVariable getVariable(String id) {
        for (RuntimeVariable runtimeVariable : getVariables()) {
            if (runtimeVariable.getId().equals(id)) return runtimeVariable;
        }

        return null;
    }

    /**
     * @return All declared variables
     */
    List<RuntimeVariable> getVariables();

    @Override
    default VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) {
        if (getVariable(id) != null) return this;
        if (getParent() == null || !(getParent() instanceof VariableDeclarationEnvironment variableDeclarationEnvironment))
            throw new InvalidIdentifierException("Variable with id " + id + " doesn't exist!");
        return variableDeclarationEnvironment.getVariableDeclarationEnvironment(id);
    }
}