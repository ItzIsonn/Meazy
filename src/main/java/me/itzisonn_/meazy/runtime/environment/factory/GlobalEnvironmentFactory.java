package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import org.jspecify.annotations.NullMarked;

/**
 * Represents factory for creating {@link GlobalEnvironment}s
 */
@NullMarked
public interface GlobalEnvironmentFactory {
    /**
     * Creates global environment
     * @return New global environment
     */
    GlobalEnvironment create();
}
