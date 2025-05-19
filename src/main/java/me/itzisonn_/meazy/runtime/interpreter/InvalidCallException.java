package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when function or constructor call is failed
 */
public class InvalidCallException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidCallException(String message) {
        super(message);
    }
}
