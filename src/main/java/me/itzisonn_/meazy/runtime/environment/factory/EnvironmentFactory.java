package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.Environment;

/**
 * Represents factory for creating {@link Environment}s
 */
public interface EnvironmentFactory {
    /**
     * Creates environment
     *
     * @param parent Parent
     * @param isShared Whether environment is shared
     * @return New environment
     */
    Environment create(Environment parent, boolean isShared);

    /**
     * Creates non-shared environment
     *
     * @param parent Parent
     * @return New environment
     */
    Environment create(Environment parent);
}
