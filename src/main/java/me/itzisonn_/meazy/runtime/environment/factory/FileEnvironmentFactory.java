package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents factory for creating {@link FileEnvironment}s
 */
@NullMarked
public interface FileEnvironmentFactory {
    /**
     * Creates file environment
     *
     * @param parent Parent
     * @param packageName Package name TODO
     * @return New file environment
     */
    FileEnvironment create(GlobalEnvironment parent, String packageName, @Nullable String className);
    /**
     * Creates file environment
     *
     * @param parent Parent
     * @param packageName Package name
     * @return New file environment
     */
    FileEnvironment create(GlobalEnvironment parent, String packageName);
}
