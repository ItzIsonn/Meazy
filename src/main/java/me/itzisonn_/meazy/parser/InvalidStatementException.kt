package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.text.TextException;
import me.itzisonn_.meazy.text.Text;
import me.itzisonn_.meazy.text.TextKt;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidStatementException extends TextException {
    public InvalidStatementException(int lineNumber, Text text) {
        super(TextKt.translatable("meazy:parser.exception.invalid_statement", lineNumber, text));
    }
}
