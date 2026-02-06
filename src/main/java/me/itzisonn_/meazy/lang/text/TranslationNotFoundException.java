package me.itzisonn_.meazy.lang.text;

import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when {@link TranslatableText} can't find translation
 */
@NullMarked
public class TranslationNotFoundException extends RuntimeException {
    /**
     * @param message Message
     */
    public TranslationNotFoundException(String message) {
        super(message);
    }
}
