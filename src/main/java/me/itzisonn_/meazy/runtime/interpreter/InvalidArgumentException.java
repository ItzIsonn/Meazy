package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when function or constructor gets an invalid argument
 */
public class InvalidArgumentException extends RuntimeException {
    /**
     * @param message Exception's message
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
