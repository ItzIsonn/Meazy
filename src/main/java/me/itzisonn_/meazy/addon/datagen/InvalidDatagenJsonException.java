package me.itzisonn_.meazy.addon.datagen;

/**
 * Is thrown when one of {@link DatagenDeserializers} meets invalid json object
 */
public class InvalidDatagenJsonException extends RuntimeException {
    /**
     * Main constructor
     * @param message Message
     */
    public InvalidDatagenJsonException(String message) {
        super(message);
    }
}
