package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.value.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Represents environment for loops TODO
 */
@NullMarked
public interface LocalVariableDeclarationEnvironment extends VariableDeclarationEnvironment {
    @Override
    VariableValue declareVariable(@Nullable String id, DataType type, boolean isConstant, @Nullable Expression value);

    //TODO
    void setStartLabel(UUID startLabel);
    void setEndLabel(UUID endLabel);
    @Nullable
    UUID getStartLabel();
    @Nullable
    UUID getEndLabel();
    int getUsedSlotsCount();
}