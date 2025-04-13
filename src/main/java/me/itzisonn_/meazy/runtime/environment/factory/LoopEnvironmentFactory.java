package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LoopEnvironment;

/**
 * Represents factory for creating {@link LoopEnvironment}s
 */
public interface LoopEnvironmentFactory {
    /**
     * Creates loop environment
     *
     * @param parent Parent
     * @param isShared Whether loop environment is shared
     * @return New loop environment
     */
    LoopEnvironment create(Environment parent, boolean isShared);

    /**
     * Creates non-shared loop environment
     *
     * @param parent Parent
     * @return New loop environment
     */
    LoopEnvironment create(Environment parent);
}
