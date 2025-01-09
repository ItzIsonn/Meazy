package me.itzisonn_.meazy.parser;

/**
 * UnexpectedTokenException is thrown when {@link ParsingFunction} meets unexpected token
 */
public class UnexpectedTokenException extends RuntimeException {
    /**
     * UnexpectedTokenException constructor
     *
     * @param message Exception's message
     */
    public UnexpectedTokenException(String message) {
        super(message);
    }

    /**
     * UnexpectedTokenException constructor. Supers message in format 'At line {@code line}: {@code message}'
     *
     * @param message Exception's message
     * @param line Line where exception occurred
     */
    public UnexpectedTokenException(String message, int line) {
        super("At line " + line + ": " + message);
    }
}
