package me.itzisonn_.meazy.runtime.environment.factory.impl;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.LocalVariableDeclarationEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class LocalVariableDeclarationEnvironmentFactoryImpl implements LocalVariableDeclarationEnvironmentFactory {
    @Override
    public LocalVariableDeclarationEnvironmentImpl create(Environment parent, @Nullable Uuid startLabel, @Nullable Uuid endLabel) {
        return new LocalVariableDeclarationEnvironmentImpl(parent, startLabel, endLabel);
    }
}
