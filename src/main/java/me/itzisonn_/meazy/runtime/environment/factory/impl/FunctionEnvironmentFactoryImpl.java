package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.FunctionEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.FunctionEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class FunctionEnvironmentFactoryImpl implements FunctionEnvironmentFactory {
    @Override
    public FunctionEnvironment create(FunctionDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, @Nullable DataType returnDataType, boolean isShared) {
        return new FunctionEnvironmentImpl(parent, startLabel, endLabel, returnDataType, isShared);
    }
}
