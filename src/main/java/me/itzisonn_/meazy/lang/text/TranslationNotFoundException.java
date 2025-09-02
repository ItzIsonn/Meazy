package me.itzisonn_.meazy.lang.text;

/**
 * Is thrown when {@link TranslatableText} can't find translation
 */
public class TranslationNotFoundException extends RuntimeException {
    /**
     * @param message Message
     */
    public TranslationNotFoundException(String message) {
        super(message);
    }
}
