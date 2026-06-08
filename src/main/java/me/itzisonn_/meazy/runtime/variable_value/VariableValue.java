package me.itzisonn_.meazy.runtime.variable_value;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Represents runtime variable value
 */
@NullMarked
public interface VariableValue {
    /**
     * @return Id
     */
    String getId();

    /**
     * @return DataType
     */
    DataType getDataType();

    /**
     * @return Whether value is constant
     */
    boolean isConstant();

    //TODO
    int getSlot();

    @Nullable
    Expression getInitializer();

    /**
     * @return Parent environment
     */
    VariableDeclarationEnvironment getParentEnvironment();



    /**
     * @param target Target modifier
     * @return Whether this runtime value has given modifier
     */
    default boolean hasModifier(Modifier target) {
        for (Modifier modifier : getModifiers()) {
            if (modifier == target) return true;
        }

        return false;
    }

    /**
     * @param id Modifier's id
     * @return Whether this runtime value has modifier with given id
     */
    default boolean hasModifier(String id) {
        for (Modifier modifier : getModifiers()) {
            if (modifier.getId().equals(id)) return true;
        }

        return false;
    }

    /**
     * @return Modifiers
     */
    Set<Modifier> getModifiers();
}