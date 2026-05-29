package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.DataType;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents environment for functions
 */
@NullMarked
public interface FunctionEnvironment extends LocalVariableDeclarationEnvironment {
    @Nullable
    DataType getReturnDataType();
    void setReturnDataType(@Nullable DataType returnDataType);

    @Override
    FunctionDeclarationEnvironment getParent();
}