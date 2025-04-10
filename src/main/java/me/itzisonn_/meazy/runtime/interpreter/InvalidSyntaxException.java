package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when {@link EvaluationFunction} finds invalid syntax
 */
public class InvalidSyntaxException extends RuntimeException {
    /**
     * @param message Exception's message
     */
    public InvalidSyntaxException(String message) {
        super(message);
    }
}
