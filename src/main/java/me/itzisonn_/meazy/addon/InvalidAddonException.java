package me.itzisonn_.meazy.addon;

/**
 * Exception when invalid addon found
 */
public class InvalidAddonException extends Exception {
    public InvalidAddonException(String message) {
        super(message);
    }

    public InvalidAddonException(Throwable cause) {
        super(cause);
    }

    public InvalidAddonException(String message, Throwable cause) {
        super(message, cause);
    }
}
