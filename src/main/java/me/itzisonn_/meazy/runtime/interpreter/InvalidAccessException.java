package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidAccessException is thrown when {@link EvaluationFunction} can't access member
 */
public class InvalidAccessException extends RuntimeException {
    /**
     * InvalidAccessException constructor
     *
     * @param message Exception's message
     */
    public InvalidAccessException(String message) {
        super(message);
    }
}
