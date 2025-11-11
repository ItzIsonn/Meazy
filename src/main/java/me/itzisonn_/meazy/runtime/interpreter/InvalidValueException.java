package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when EvaluationFunction encounters invalid value
 */
public class InvalidValueException extends TextException {
    /**
     * @param text Text
     */
    public InvalidValueException(Text text) {
        super(text);
    }
}
