package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link EvaluationFunction} finds invalid action
 */
public class InvalidActionException extends TextException {
    /**
     * @param text Text
     */
    public InvalidActionException(Text text) {
        super(text);
    }
}
