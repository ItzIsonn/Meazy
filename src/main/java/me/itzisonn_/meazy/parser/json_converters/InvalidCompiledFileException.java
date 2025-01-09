package me.itzisonn_.meazy.parser.json_converters;

import me.itzisonn_.meazy.registry.RegistryIdentifier;

/**
 * InvalidCompiledFileException is thrown when {@link Converter} meets invalid json object
 */
public class InvalidCompiledFileException extends RuntimeException {
    /**
     * InvalidCompiledFileException constructor
     *
     * @param message Exception's message
     */
    public InvalidCompiledFileException(String message) {
        super(message);
    }

    /**
     * InvalidCompiledFileException constructor. Supers message in format '{@code identifier} doesn't have field {@code field}'
     *
     * @param identifier Identifier
     * @param field Field
     */
    public InvalidCompiledFileException(RegistryIdentifier identifier, String field) {
        this(identifier + " doesn't have have field " + field);
    }
}
