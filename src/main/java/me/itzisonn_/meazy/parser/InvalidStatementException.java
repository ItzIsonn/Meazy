package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidStatementException extends TextException {
    public InvalidStatementException(int lineNumber, Text text) {
        super(Text.translatable("meazy:parser.exception.invalid_statement", lineNumber, text));
    }
}
