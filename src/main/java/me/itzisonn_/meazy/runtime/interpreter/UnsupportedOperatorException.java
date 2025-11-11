package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link EvaluationFunction} can't evaluate expression because of unknown operator
 */
public class UnsupportedOperatorException extends TextException {
    /**
     * Supers message in format 'Can't evaluate expression with operator {@code operator}'
     * @param operator Operator
     */
    public UnsupportedOperatorException(String operator) {
        super(Text.translatable("meazy:runtime.operator").append(Text.literal(operator)));
    }
}
