package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidSyntaxException is thrown when {@link EvaluationFunction} finds invalid syntax
 */
public class InvalidSyntaxException extends RuntimeException {
    /**
     * InvalidSyntaxException constructor
     *
     * @param message Exception's message
     */
    public InvalidSyntaxException(String message) {
        super(message);
    }
}
