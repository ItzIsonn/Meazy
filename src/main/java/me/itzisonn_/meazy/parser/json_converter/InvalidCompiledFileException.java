package me.itzisonn_.meazy.parser.json_converter;

import me.itzisonn_.registry.RegistryIdentifier;

/**
 * Is thrown when {@link Converter} meets invalid json object
 */
public class InvalidCompiledFileException extends RuntimeException {
    /**
     * Main constructor
     * @param message Message
     */
    public InvalidCompiledFileException(String message) {
        super(message);
    }

    /**
     * Constructor that supers message in format '{@code identifier} doesn't have field {@code field}'
     *
     * @param identifier Identifier
     * @param field Field
     */
    public InvalidCompiledFileException(RegistryIdentifier identifier, String field) {
        this(identifier + " doesn't have have field " + field);
    }
}
