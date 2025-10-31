package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link ParsingFunction} meets unexpected token
 */
public class UnexpectedTokenException extends TextException {
    /**
     * @param lineNumber Line number
     * @param text Text
     */
    public UnexpectedTokenException(int lineNumber, Text text) {
        super(Text.translatable("meazy:parser.unexpected_token", lineNumber, text));
    }
}
