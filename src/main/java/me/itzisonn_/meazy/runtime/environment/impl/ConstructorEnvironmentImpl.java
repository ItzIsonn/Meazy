package me.itzisonn_.meazy.runtime.environment.impl;

import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ConstructorEnvironmentImpl extends LocalVariableDeclarationEnvironmentImpl implements ConstructorEnvironment {
    public ConstructorEnvironmentImpl(ConstructorDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel) {
        super(parent, startLabel, endLabel);
    }

    @Override
    public boolean isShared() {
        return false;
    }

    @Override
    public ConstructorDeclarationEnvironment getParent() {
        return (ConstructorDeclarationEnvironment) parent;
    }
}