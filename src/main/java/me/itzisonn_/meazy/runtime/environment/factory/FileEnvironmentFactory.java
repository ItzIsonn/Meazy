package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.FileEnvironment;
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;

/**
 * Represents factory for creating {@link FileEnvironment}s
 */
@NullMarked
public interface FileEnvironmentFactory {
    /**
     * Creates file environment
     *
     * @param parent Parent
     * @param parentFile Parent file
     * @return New file environment
     */
    FileEnvironment create(GlobalEnvironment parent, @Nullable File parentFile);
}
