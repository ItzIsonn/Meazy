package me.itzisonn_.meazy.parser;

/**
 * Is thrown when {@link ParsingFunction} meets unexpected token
 */
public class UnexpectedTokenException extends RuntimeException {
    /**
     * Main constructor
     * @param message Message
     */
    public UnexpectedTokenException(String message) {
        super(message);
    }

    /**
     * Constructor that supers message in format 'At line {@code line}: {@code message}'
     *
     * @param message Message
     * @param line Line where exception occurred
     */
    public UnexpectedTokenException(String message, int line) {
        super("At line " + line + ": " + message);
    }
}
