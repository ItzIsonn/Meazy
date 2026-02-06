package me.itzisonn_.meazy.addon.datagen;

import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when one of {@link DatagenDeserializers} meets invalid JSON object
 */
@NullMarked
public class InvalidDatagenJsonException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidDatagenJsonException(String message) {
        super(message);
    }
}
