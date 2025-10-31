package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link ParsingFunction} can't parse statement
 */
public class InvalidStatementException extends TextException {
    /**
     * @param lineNumber Line number
     * @param text Text
     */
    public InvalidStatementException(int lineNumber, Text text) {
        super(Text.translatable("meazy:parser.invalid_statement", lineNumber, text));
    }
}
