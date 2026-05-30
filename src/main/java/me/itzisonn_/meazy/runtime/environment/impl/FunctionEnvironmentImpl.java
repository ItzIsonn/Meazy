package me.itzisonn_.meazy.runtime.environment.impl;

import lombok.Getter;
import lombok.Setter;
import me.itzisonn_.meazy.parser.DataType;
import me.itzisonn_.meazy.parser.modifier.Modifier;
import me.itzisonn_.meazy.runtime.environment.FunctionDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.FunctionEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NullMarked
public class FunctionEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements FunctionEnvironment {
    @Getter
    @Setter
    @Nullable
    private DataType returnDataType;
    @Getter
    private final boolean isShared;
    protected final Set<Modifier> modifiers;

    public FunctionEnvironmentImpl(FunctionDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel, @Nullable DataType returnDataType, boolean isShared, Set<Modifier> modifiers) {
        super(parent, startLabel, endLabel);
        this.returnDataType = returnDataType;
        this.isShared = isShared;
        this.modifiers = modifiers;
    }

    @Override
    public FunctionDeclarationEnvironment getParent() {
        return (FunctionDeclarationEnvironment) parent;
    }

    @Override
    public Set<Modifier> getModifiers() {
        return new HashSet<>(modifiers);
    }
}