package me.itzisonn_.meazy.runtime.environment;

import lombok.Getter;
import me.itzisonn_.meazy.parser.ast.DataType;
import me.itzisonn_.meazy.runtime.interpreter.InvalidSyntaxException;
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
     * @param dataType Variable's DataType
     * @param value Variable's value
     * @param isConstant Whether is this variable constant
     * @param accessModifiers Variable's access modifiers
     */
    void declareVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers);

    /**
     * Declares argument with given data in this environment
     *
     * @param id Argument's id
     * @param dataType Argument's DataType
     * @param value Argument's value
     * @param isConstant Whether is this argument constant
     * @param accessModifiers Argument's access modifiers
     */
    void declareArgument(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers);

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
        if (getParent() == null || !(getParent() instanceof VariableDeclarationEnvironment variableDeclarationEnvironment)) return null;
        return variableDeclarationEnvironment.getVariableDeclarationEnvironment(id);
    }



    @Getter
    class RuntimeVariable {
        private final String id;
        private final DataType dataType;
        private RuntimeValue<?> value;
        private final boolean isConstant;
        private final Set<String> accessModifiers;
        private final boolean isArgument;

        public RuntimeVariable(String id, DataType dataType, RuntimeValue<?> value, boolean isConstant, Set<String> accessModifiers, boolean isArgument) {
            this.id = id;
            this.dataType = dataType;

            setValue(value);

            this.isConstant = isConstant;
            this.accessModifiers = accessModifiers;
            this.isArgument = isArgument;
        }

        public void setValue(RuntimeValue<?> value) {
            if (isConstant && this.value != null && this.value.getFinalValue() != null)
                throw new InvalidSyntaxException("Can't reassign value of constant variable " + id);

            if (!dataType.isMatches(value)) throw new InvalidSyntaxException("Variable with id " + id + " requires data type " + dataType.getId());
            this.value = value;
        }
    }
}