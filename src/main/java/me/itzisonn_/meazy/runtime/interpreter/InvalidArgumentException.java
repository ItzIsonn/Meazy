package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when function or constructor gets an invalid argument
 */
public class InvalidArgumentException extends TextException {
    /**
     * @param text Text
     */
    public InvalidArgumentException(Text text) {
        super(text);
    }
}
