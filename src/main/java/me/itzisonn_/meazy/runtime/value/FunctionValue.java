package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Represents function value
 */
@NullMarked
public interface FunctionValue extends ModifierableRuntimeValue {
    /**
     * @return Id
     */
    String getId();

    /**
     * @return Parameters
     */
    List<ParameterExpression> getParameters();

    /**
     * @return Which DataType should this function return
     */
    @Nullable
    DataType getReturnDataType();

    /**
     * @return Parent environment tODO
     */
    FunctionEnvironment getEnvironment();



    /**
     * @return Whether this function is overridden
     */
    boolean isOverridden();

    /**
     * Makes this function overridden (can't be undone)
     */
    void setOverridden();



    /**
     * Returns whether this function has same id, args and returnDataType
     *
     * @param o Object to compare
     * @return Whether this function is like o
     */
    boolean isLike(Object o);
}
