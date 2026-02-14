package me.itzisonn_.meazy.runtime.environment;

import org.jspecify.annotations.NullMarked;

import java.util.UUID;

/**
 * Represents environment for loops
 */
@NullMarked
public interface LoopEnvironment extends LocalVariableDeclarationEnvironment {
    @Override
    UUID getStartLabel();
    @Override
    UUID getEndLabel();
}