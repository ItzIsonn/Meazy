package me.itzisonn_.meazy.settings;

import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when {@link SettingsDeserializer} meets invalid settings file
 */
@NullMarked
public class InvalidSettingsException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidSettingsException(String message) {
        super(message);
    }
}
