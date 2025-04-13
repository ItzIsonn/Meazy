package me.itzisonn_.meazy.runtime;

import me.itzisonn_.meazy.parser.ast.Program;

/**
 * Is thrown when {@link Program} can't import file
 */
public class InvalidFileException extends RuntimeException {
    /**
     * @param message Exception's message
     */
    public InvalidFileException(String message) {
        super(message);
    }
}
