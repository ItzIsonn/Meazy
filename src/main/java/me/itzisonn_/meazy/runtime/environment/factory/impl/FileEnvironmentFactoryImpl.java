package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.FileEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.FileEnvironmentFactory;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class FileEnvironmentFactoryImpl implements FileEnvironmentFactory {
    @Override
    public FileEnvironment create(GlobalEnvironment parent, String packageName, @Nullable String className) {
        return new FileEnvironmentImpl(parent, packageName, className);
    }

    @Override
    public FileEnvironment create(GlobalEnvironment parent, String packageName) {
        return new FileEnvironmentImpl(parent, packageName, null);
    }
}
