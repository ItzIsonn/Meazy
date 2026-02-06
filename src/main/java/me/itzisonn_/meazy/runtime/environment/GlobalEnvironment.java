package me.itzisonn_.meazy.runtime.environment;

import org.jspecify.annotations.NullMarked;

import java.util.Set;

/**
 * Represents global environment
 */
@NullMarked
public interface GlobalEnvironment extends Environment {
    /**
     * Adds to this global environment file environment
     * @param fileEnvironment FileEnvironment to add
     */
    void addFileEnvironment(FileEnvironment fileEnvironment);

    /**
     * @return All file environments
     */
    Set<FileEnvironment> getFileEnvironments();
}