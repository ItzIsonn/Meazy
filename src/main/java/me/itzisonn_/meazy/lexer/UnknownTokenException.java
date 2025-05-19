package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.Registries;

/**
 * Is thrown when {@link Registries#TOKENIZATION_FUNCTION} can't recognize token
 */
public class UnknownTokenException extends RuntimeException {
    /**
     * @param message Message
     */
    public UnknownTokenException(String message) {
        super(message);
    }
}