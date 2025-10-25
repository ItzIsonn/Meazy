package me.itzisonn_.meazy.addon;

/**
 * Exception when trying to load an invalid Addon file
 */
public class UnknownDependencyException extends RuntimeException {
    public UnknownDependencyException(String message) {
        super(message);
    }
}
