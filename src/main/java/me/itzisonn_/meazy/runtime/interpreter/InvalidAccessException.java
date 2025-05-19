package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when {@link EvaluationFunction} can't access member
 */
public class InvalidAccessException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidAccessException(String message) {
        super(message);
    }
}
