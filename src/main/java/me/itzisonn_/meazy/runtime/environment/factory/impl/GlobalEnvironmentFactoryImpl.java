package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.GlobalEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.GlobalEnvironmentFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class GlobalEnvironmentFactoryImpl implements GlobalEnvironmentFactory {
    @Override
    public GlobalEnvironment create() {
        return new GlobalEnvironmentImpl();
    }
}
