package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.impl.LocalVariableDeclarationEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.LocalVariableDeclarationEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class LocalVariableDeclarationEnvironmentFactoryImpl implements LocalVariableDeclarationEnvironmentFactory {
    @Override
    public LocalVariableDeclarationEnvironmentImpl create(Environment parent, @Nullable UUID startLabel, @Nullable UUID endLabel) {
        return new LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel);
    }
}
