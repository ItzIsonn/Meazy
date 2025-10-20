package me.itzisonn_.meazy.runtime.environment;

import me.itzisonn_.meazy.context.RuntimeContext;

import java.util.Set;

/**
 * Represents global environment
 */
public interface GlobalEnvironment extends Environment {
    /**
     * @return Context containing this global environment
     */
    RuntimeContext getContext();



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