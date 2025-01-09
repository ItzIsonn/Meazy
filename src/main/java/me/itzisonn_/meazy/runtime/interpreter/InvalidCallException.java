package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidCallException is thrown when function or constructor call is failed
 */
public class InvalidCallException extends RuntimeException {
    /**
     * InvalidCallException constructor
     *
     * @param message Exception's message
     */
    public InvalidCallException(String message) {
        super(message);
    }
}
