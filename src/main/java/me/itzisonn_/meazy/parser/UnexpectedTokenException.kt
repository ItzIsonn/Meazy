package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.text.TextException;
import me.itzisonn_.meazy.text.Text;
import me.itzisonn_.meazy.text.TextKt;
import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when {@link ParsingFunction} meets unexpected token
 */
@NullMarked
public class UnexpectedTokenException extends TextException {
    /**
     * @param lineNumber Line number
     * @param text Text
     */
    public UnexpectedTokenException(int lineNumber, Text text) {
        super(TextKt.translatable("meazy:parser.unexpected_token", lineNumber, text));
    }
}
