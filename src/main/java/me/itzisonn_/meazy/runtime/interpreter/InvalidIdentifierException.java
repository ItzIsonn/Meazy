package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when {@link EvaluationFunction} can't find object with requested identifier
 */
public class InvalidIdentifierException extends RuntimeException {
    /**
     * @param message Exception's message
     */
    public InvalidIdentifierException(String message) {
        super(message);
    }
}
