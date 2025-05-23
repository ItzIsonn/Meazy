package me.itzisonn_.meazy.runtime.interpreter;

/**
 * Is thrown when {@link EvaluationFunction} can't evaluate expression because of unknown operator
 */
public class UnsupportedOperatorException extends RuntimeException {
    /**
     * Supers message in format 'Can't evaluate expression with operator {@code operator}'
     * @param operator Operator
     */
    public UnsupportedOperatorException(String operator) {
        super("Can't evaluate expression with operator " + operator);
    }
}
