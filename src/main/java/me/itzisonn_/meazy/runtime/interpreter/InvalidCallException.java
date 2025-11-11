package me.itzisonn_.meazy.runtime.interpreter;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when function or constructor call is failed
 */
public class InvalidCallException extends TextException {
    /**
     * @param text Text
     */
    public InvalidCallException(Text text) {
        super(text);
    }
}
