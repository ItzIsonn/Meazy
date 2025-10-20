package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;

import java.io.File;

/**
 * Represents factory for creating {@link FileEnvironment}s
 */
public interface FileEnvironmentFactory {
    /**
     * Creates file environment
     *
     * @param parent Parent
     * @param parentFile Parent file
     * @return New file environment
     */
    FileEnvironment create(GlobalEnvironment parent, File parentFile);
}
