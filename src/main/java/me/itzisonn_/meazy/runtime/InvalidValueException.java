package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when EvaluationFunction encounters invalid value
 */
@NullMarked
public class InvalidValueException extends TextException {
    /**
     * @param text Text
     */
    public InvalidValueException(Text text) {
        super(text);
    }
}
