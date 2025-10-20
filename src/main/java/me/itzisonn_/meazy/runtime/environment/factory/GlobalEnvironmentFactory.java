package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.context.RuntimeContext;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;

/**
 * Represents factory for creating {@link GlobalEnvironment}s
 */
public interface GlobalEnvironmentFactory {
    /**
     * Creates global environment
     *
     * @param context Runtime Context
     * @return New global environment
     */
    GlobalEnvironment create(RuntimeContext context);
}
