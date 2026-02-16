package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class FunctionEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements FunctionEnvironment {
    @Getter
    @Nullable
    private final DataType returnDataType;
    @Getter
    private final boolean isShared;

    public FunctionEnvironmentImpl(FunctionDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, @Nullable DataType returnDataType, boolean isShared) {
        super(parent, startLabel, endLabel);
        this.returnDataType = returnDataType;
        this.isShared = isShared;
    }

    @Override
    public FunctionDeclarationEnvironment getParent() {
        return (FunctionDeclarationEnvironment) parent;
    }
}