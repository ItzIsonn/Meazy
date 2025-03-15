package me.itzisonn_.meazy.addon;

/**
 * Exception when invalid addon.json found
 */
public class InvalidAddonInfoException extends Exception {
    public InvalidAddonInfoException(String message) {
        super(message);
    }

    public InvalidAddonInfoException(Throwable cause) {
        super("Invalid addon.json", cause);
    }

    public InvalidAddonInfoException(Throwable cause, String message) {
        super(message, cause);
    }
}