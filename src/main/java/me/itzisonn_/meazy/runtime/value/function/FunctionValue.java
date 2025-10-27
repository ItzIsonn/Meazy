package me.itzisonn_.meazy.runtime.value.function;

import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.expression.CallArgExpression;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.value.ModifierableRuntimeValue;

import java.util.List;

/**
 * Represents function value
 */
public interface FunctionValue extends ModifierableRuntimeValue<Object> {
    /**
     * @return Id
     */
    String getId();

    /**
     * @return Args
     */
    List<CallArgExpression> getArgs();

    /**
     * @return Which DataType should this function return
     */
    DataType getReturnDataType();

    /**
     * @return Parent environment
     */
    FunctionDeclarationEnvironment getParentEnvironment();



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
