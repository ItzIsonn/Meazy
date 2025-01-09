package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidIdentifierException is thrown when {@link EvaluationFunction} can't find object with requested identifier
 */
public class InvalidIdentifierException extends RuntimeException {
    /**
     * InvalidIdentifierException constructor
     *
     * @param message Exception's message
     */
    public InvalidIdentifierException(String message) {
        super(message);
    }
}
