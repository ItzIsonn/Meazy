package me.itzisonn_.meazy.runtime.environment;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.parser.ast.expression.Expression;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.VariableValue;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Adds to Environment ability to declare local variables
 */
@NullMarked
public interface LocalVariableDeclarationEnvironment extends VariableDeclarationEnvironment {
    @Override
    VariableValue declareVariable(@Nullable String id, DataType type, boolean isConstant, @Nullable Expression value);

    //TODO
    void setStartLabel(Uuid startLabel);
    void setEndLabel(Uuid endLabel);
    @Nullable
    Uuid getStartLabel();
    @Nullable
    Uuid getEndLabel();
    int getUsedSlotsCount();
}