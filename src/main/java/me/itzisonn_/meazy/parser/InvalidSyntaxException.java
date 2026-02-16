package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class InvalidSyntaxException extends TextException {
    public InvalidSyntaxException(int lineNumber, Text text) {
        super(Text.translatable("meazy:parser.exception.invalid_syntax", lineNumber, text));
    }
}
