package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.FileEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.FileEnvironmentFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class FileEnvironmentFactoryImpl implements FileEnvironmentFactory {
    @Override
    public FileEnvironment create(GlobalEnvironment parent, String packageName, String className) {
        return new FileEnvironmentImpl(parent, packageName, className);
    }
}
