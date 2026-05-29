package me.itzisonn_.meazy.runtime.environment;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents an environment
 */
@NullMarked
public interface Environment {
    /**
     * @return This environment's parent
     */
    @Nullable
    Environment getParent();

    /**
     * @return Whether this environment is shared
     */
    boolean isShared();

    @Nullable
    default String getFullClassName() {
        return null;
    }
}