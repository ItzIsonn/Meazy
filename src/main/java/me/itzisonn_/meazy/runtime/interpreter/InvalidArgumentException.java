package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidArgumentException is thrown when function or constructor gets an invalid argument
 */
public class InvalidArgumentException extends RuntimeException {
    /**
     * InvalidArgumentException constructor
     *
     * @param message Exception's message
     */
    public InvalidArgumentException(String message) {
        super(message);
    }
}
