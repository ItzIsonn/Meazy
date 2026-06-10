package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
    default Optional<VariableValue> getVariable(String id) {
        for (VariableValue variableValue : getVariables()) {
            if (id.equals(variableValue.getId())) return Optional.of(variableValue);
        }

        return Optional.empty();
    }

    /**
     * @return All declared variables
     */
    List<VariableValue> getVariables();
}