package me.itzisonn_.meazy.lang;

import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

/**
 * Exception that accepts {@link Text} instead of {@link String}
 */
@NullMarked
public abstract class TextException extends RuntimeException {
    /**
     * @param text Text
     */
    public TextException(Text text) {
        super(text.toString());
    }
}
