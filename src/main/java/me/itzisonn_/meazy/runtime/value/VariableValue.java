package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.VariableDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents runtime variable value
 */
@NullMarked
public interface VariableValue extends ModifierableRuntimeValue {
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
}