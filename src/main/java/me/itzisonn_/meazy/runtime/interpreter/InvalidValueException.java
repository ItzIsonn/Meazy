package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when EvaluationFunction encounters invalid value
 */
public class InvalidValueException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidValueException(String message) {
        super(message);
    }
}
