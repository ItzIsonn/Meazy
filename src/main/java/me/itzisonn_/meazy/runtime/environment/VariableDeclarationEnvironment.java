package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Adds to Environment ability to declare variables
 */
@NullMarked
public interface VariableDeclarationEnvironment extends Environment {
    /**
     * Declares given VariableValue in this environment
     * @param id VariableValue TODO
     */
    VariableValue declareVariable(String id, DataType type, boolean isConstant, @Nullable Expression value);

    /**
     * @param id Variable's id
     * @return Declared variable with given id
     */
    @Nullable
    default VariableValue getVariable(String id) {
        for (VariableValue variableValue : getVariables()) {
            if (id.equals(variableValue.getId())) return variableValue;
        }

        return null;
    }

    /**
     * @return All declared variables
     */
    List<VariableValue> getVariables();



    @Override
    @Nullable
    default VariableDeclarationEnvironment getVariableDeclarationEnvironment(String id) {
        VariableValue variableValue = getVariable(id);
        if (variableValue != null) return variableValue.getParentEnvironment();

        Environment parent = getParent();
        if (parent == null) return null;
        return parent.getVariableDeclarationEnvironment(id);
    }
}