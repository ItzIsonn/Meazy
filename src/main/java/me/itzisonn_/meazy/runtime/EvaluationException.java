package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

/**
 * Is thrown TODO
 */
@NullMarked
public class EvaluationException extends TextException {
    /**
     * @param text Text
     */
    public EvaluationException(Text text) {
        super(text);
    }
}
