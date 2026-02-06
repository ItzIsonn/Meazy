package me.itzisonn_.meazy.addon;

import org.jspecify.annotations.NullMarked;

/**
 * Exception when trying to load an invalid Addon file
 */
@NullMarked
public class UnknownDependencyException extends RuntimeException {
    public UnknownDependencyException(String message) {
        super(message);
    }
}
