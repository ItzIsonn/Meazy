package me.itzisonn_.meazy.lang;

import me.itzisonn_.meazy.lang.text.Text;

/**
 * Exception that accepts {@link Text} instead of {@link String}
 */
public abstract class TextException extends RuntimeException {
    /**
     * @param text Text
     * @throws NullPointerException If given text is null
     */
    public TextException(Text text) throws NullPointerException {
        if (text == null) throw new NullPointerException("Text can't be null");
        super(text.toString());
    }
}
