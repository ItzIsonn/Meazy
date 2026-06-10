package me.itzisonn_.meazy.runtime.environment.factory.impl;

import kotlin.uuid.Uuid;
import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironmentImpl;
import me.itzisonn_.meazy.runtime.environment.factory.LoopEnvironmentFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class LoopEnvironmentFactoryImpl implements LoopEnvironmentFactory {
    @Override
    public LoopEnvironment create(Environment parent, Uuid startLabel, Uuid endLabel) {
        return new LoopEnvironmentImpl(parent, startLabel, endLabel);
    }
}
