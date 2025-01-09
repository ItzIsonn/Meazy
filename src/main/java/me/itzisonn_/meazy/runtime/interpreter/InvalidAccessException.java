package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidAccessException is thrown when {@link EvaluationFunction} can't access class member because of it's access modifiers
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
