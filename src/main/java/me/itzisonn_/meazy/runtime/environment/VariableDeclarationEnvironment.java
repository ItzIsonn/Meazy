package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.lang.text.Text;
import me.itzisonn_.meazy.runtime.interpreter.InvalidIdentifierException;
import me.itzisonn_.meazy.runtime.value.RuntimeValue;
import me.itzisonn_.meazy.runtime.value.VariableValue;

import java.util.Set;

/**
 * Adds to Environment ability to declare variables
 */
public interface VariableDeclarationEnvironment extends Environment {
    /**
     * Declares given VariableValue in this environment
     * @param value VariableValue
     */
    void declareVariable(VariableValue value);

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     * @throws NullPointerException If given id is null
     */
    default VariableValue getVariable(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (VariableValue variableValue : getVariables()) {
            if (variableValue.getId().equals(id)) return variableValue;
        }

        return null;
    }

    /**
     * Assigns value to existing non-constant variable
     *
     * @param id Variable's id
     * @param value Variable's new value
     *
     * @throws NullPointerException If either id or value is null
     * @throws InvalidIdentifierException If can't find variable with given id
     */
    default void assignVariable(String id, RuntimeValue<?> value) throws NullPointerException, InvalidIdentifierException {
        if (id == null) throw new NullPointerException("Id can't be null");
        if (value == null) throw new NullPointerException("Value can't be null");

        VariableValue variableValue = getVariable(id);
        if (variableValue == null) throw new InvalidIdentifierException(Text.translatable("meazy:runtime.variable.cant_find", id));

        variableValue.setValue(value);
    }

    /**
     * @return All declared variables
     */
    Set<VariableValue> getVariables();



    @Override
    default VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) throws NullPointerException {
        if (id == null) throw new NullPointerException("Id can't be null");

        VariableValue variableValue = getVariable(id);
        if (variableValue != null) return variableValue.getParentEnvironment();

        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getVariableDeclarationEnvironment(id);
    }
}