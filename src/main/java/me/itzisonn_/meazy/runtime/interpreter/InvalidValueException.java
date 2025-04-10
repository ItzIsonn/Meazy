package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when EvaluationFunction encounters invalid value
 */
public class InvalidValueException extends RuntimeException {
    /**
     * @param message Exception's message
     */
    public InvalidValueException(String message) {
        super(message);
    }
}
