package me.itzisonn_.meazy.addon;

/**
 * Is thrown when invalid addon.json found
 */
public class InvalidAddonInfoException extends Exception {
    public InvalidAddonInfoException(String message) {
        super("Invalid addon.json: " + message);
    }

    public InvalidAddonInfoException(Throwable cause) {
        super("Invalid addon.json", cause);
    }
}