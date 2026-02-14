package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Represents factory for creating {@link FunctionEnvironment}s
 */
@NullMarked
public interface FunctionEnvironmentFactory { //TODO javadoc
    /**
     * Creates function environment
     *
     * @param parent Parent
     * @param isShared Whether function environment is shared
     * @return New function environment
     */
    FunctionEnvironment create(FunctionDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, @Nullable DataType returnDataType, boolean isShared);
}
