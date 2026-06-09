package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.text.TextException;
import me.itzisonn_.meazy.text.Text;
import me.itzisonn_.meazy.text.TextKt;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidSyntaxException extends TextException {
    public InvalidSyntaxException(int lineNumber, Text text) {
        super(TextKt.translatable("meazy:parser.exception.invalid_syntax", lineNumber, text));
    }
}
