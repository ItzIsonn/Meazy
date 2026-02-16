package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.runtime.environment.ConstructorDeclarationEnvironment;
import me.itzisonn_.meazy.runtime.environment.ConstructorEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.ConstructorEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.ConstructorEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public class ConstructorEnvironmentFactoryImpl implements ConstructorEnvironmentFactory {
    @Override
    public ConstructorEnvironment create(ConstructorDeclarationEnvironment parent, @Nullable UUID startLabel, @Nullable UUID endLabel) {
        return new ConstructorEnvironmentImpl(parent, startLabel, endLabel);
    }
}
