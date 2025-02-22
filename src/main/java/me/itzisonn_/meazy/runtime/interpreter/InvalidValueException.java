package me.itzisonn_.meazy.runtime.interpreter;

/**
 * InvalidValueException is thrown when EvaluationFunction encounters invalid value
 */
public class InvalidValueException extends RuntimeException {
    /**
     * InvalidValueException constructor
     *
     * @param message Exception's message
     */
    public InvalidValueException(String message) {
        super(message);
    }
}
