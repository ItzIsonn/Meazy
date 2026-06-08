package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.ast.expression.ParameterExpression;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Represents environment for functions
 */
@NullMarked
public interface FunctionEnvironment extends LocalVariableDeclarationEnvironment, ModifieredEnvironment {
    /**
     * @return Id
     */
    String getId();

    /**
     * @return Parameters
     */
    List<ParameterExpression> getParameters();

    @Nullable
    DataType getReturnDataType();
    void setReturnDataType(@Nullable DataType returnDataType);

    @Override
    FunctionDeclarationEnvironment getParent();
}