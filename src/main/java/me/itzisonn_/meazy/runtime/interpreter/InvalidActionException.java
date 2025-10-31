package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when {@link EvaluationFunction} finds invalid action
 */
public class InvalidActionException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidActionException(String message) {
        super(message);
    }
}
