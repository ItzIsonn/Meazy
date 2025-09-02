package me.itzisonn_.meazy.settings;

/**
 * Is thrown when {@link SettingsDeserializer} meets invalid settings file
 */
public class InvalidSettingsException extends RuntimeException {
    /**
     * @param message Message
     */
    public InvalidSettingsException(String message) {
        super(message);
    }
}
