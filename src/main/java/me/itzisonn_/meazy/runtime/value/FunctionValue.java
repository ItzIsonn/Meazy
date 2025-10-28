package me.itzisonn_.meazy.runtime.value;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.parser.data_type.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;

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
     * @return Parameters
     */
    List<ParameterExpression> getParameters();

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



    /**
     * Runs this function with given args and environment
     *
     * @param context Runtime context
     * @param functionEnvironment Unique Environment of this function
     * @param callEnvironment Environment from which this function is called
     * @param args Args given to this function
     *
     * @return Function return value
     */
    RuntimeValue<?> run(RuntimeContext context, FunctionEnvironment functionEnvironment, Environment callEnvironment, List<RuntimeValue<?>> args);
}
