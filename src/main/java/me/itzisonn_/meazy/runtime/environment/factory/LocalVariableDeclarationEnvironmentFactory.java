package me.itzisonn_.meazy.runtime.environment.factory;

import me.itzisonn_.meazy.runtime.environment.Environment;
import me.itzisonn_.meazy.runtime.environment.LocalVariableDeclarationEnvironment;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Represents factory for creating {@link LocalVariableDeclarationEnvironment}s
 */
@NullMarked
public interface LocalVariableDeclarationEnvironmentFactory {//TODO
    /**
     * Creates non-shared loop environment
     *
     * @param parent Parent
     * @return New loop environment
     */
    LocalVariableDeclarationEnvironment create(Environment parent, @Nullable UUID startLabel, @Nullable UUID endLabel);
}
