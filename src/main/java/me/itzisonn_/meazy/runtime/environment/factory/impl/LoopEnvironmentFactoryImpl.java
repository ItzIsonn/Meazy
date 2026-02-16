package me.itzisonn_.meazy.runtime.environment.factory.impl;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.impl.LoopEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.LoopEnvironmentFactory;
import org.jspecify.annotations.NullMarked;

import java.util.UUID;

@NullMarked
public class LoopEnvironmentFactoryImpl implements LoopEnvironmentFactory {
    @Override
    public LoopEnvironment create(Environment parent, UUID startLabel, UUID endLabel) {
        return new LoopEnvironmentImpl(parent, startLabel, endLabel);
    }
}
