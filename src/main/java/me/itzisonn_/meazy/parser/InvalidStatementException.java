package me.itzisonn_.meazy.parser;

/**
 * InvalidStatementException is thrown when {@link ParsingFunction} can't parse statement
 */
public class InvalidStatementException extends RuntimeException {
    /**
     * InvalidStatementException constructor
     *
     * @param message Exception's message
     */
    public InvalidStatementException(String message) {
        super(message);
    }

    /**
     * InvalidStatementException constructor. Supers message in format 'At line {@code line}: {@code message}'
     *
     * @param message Exception's message
     * @param line Line where exception occurred
     */
    public InvalidStatementException(String message, int line) {
        super("At line " + line + ": " + message);
    }
}
