package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.text.TextException;
import me.itzisonn_.meazy.text.Text;
import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when resolver can't find object with requested identifier
 */
@NullMarked
public class InvalidIdentifierException extends TextException {
    /**
     * @param text Text
     */
    public InvalidIdentifierException(Text text) {
        super(text);
    }
}
