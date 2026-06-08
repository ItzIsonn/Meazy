package me.itzisonn_.meazy.runtime.environment;

import kotlin.uuid.Uuid;
import org.jspecify.annotations.NullMarked;

/**
 * Represents environment for loops
 */
@NullMarked
public interface LoopEnvironment extends LocalVariableDeclarationEnvironment {
    @Override
    Uuid getStartLabel();
    @Override
    Uuid getEndLabel();
}