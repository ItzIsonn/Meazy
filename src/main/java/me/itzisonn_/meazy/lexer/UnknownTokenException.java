package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.Registries;

/**
 * UnknownTokenException is thrown when {@link Registries#TOKENIZATION_FUNCTION} can't recognize token
 */
public class UnknownTokenException extends RuntimeException {
    /**
     * UnknownTokenException constructor
     *
     * @param message Exception's message
     */
    public UnknownTokenException(String message) {
        super(message);
    }
}