package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;

import java.io.File;

/**
 * Represents factory for creating {@link GlobalEnvironment}s
 */
public interface GlobalEnvironmentFactory {
    /**
     * Creates global environment
     *
     * @param parentFile Parent file
     * @return New global environment
     */
    GlobalEnvironment create(File parentFile);
}
