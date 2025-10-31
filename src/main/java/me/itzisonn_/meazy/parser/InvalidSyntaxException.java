package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link ParsingFunction} finds invalid syntax
 */
public class InvalidSyntaxException extends TextException {
    /**
     * @param lineNumber Line number
     * @param text Text
     */
    public InvalidSyntaxException(int lineNumber, Text text) {
        super(Text.translatable("meazy:parser.invalid_syntax", lineNumber, text));
    }
}
