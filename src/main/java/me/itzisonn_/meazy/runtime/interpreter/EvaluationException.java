package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown by {@link EvaluationFunction}
 */
public class EvaluationException extends TextException {
    /**
     * @param text Text
     */
    public EvaluationException(Text text) {
        super(text);
    }
}
