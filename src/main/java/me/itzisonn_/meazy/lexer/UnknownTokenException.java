package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.text.TextKt;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.text.TextException;
import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when {@link Registries#TOKENIZATION_FUNCTION} can't recognize token
 */
@NullMarked
public class UnknownTokenException extends TextException {
    /**
     * @param lineNumber Line number
     * @param errorString Error string
     */
    public UnknownTokenException(int lineNumber, String errorString) {
        super(TextKt.translatable("meazy:lexer.unknown_token", lineNumber, errorString));
    }
}