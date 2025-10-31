package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.lang.TextException;
import me.itzisonn_.meazy.lang.text.Text;

/**
 * Is thrown when {@link Registries#TOKENIZATION_FUNCTION} can't recognize token
 */
public class UnknownTokenException extends TextException {
    /**
     * @param lineNumber Line number
     * @param errorString Error string
     */
    public UnknownTokenException(int lineNumber, String errorString) {
        super(Text.translatable("meazy:lexer.unknown_token", lineNumber, errorString));
    }
}