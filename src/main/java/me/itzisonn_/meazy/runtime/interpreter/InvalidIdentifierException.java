package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link EvaluationFunction} can't find object with requested identifier
 */
public class InvalidIdentifierException extends TextException {
    /**
     * @param text Text
     */
    public InvalidIdentifierException(Text text) {
        super(text);
    }
}
