package me.itzisonn_.meazy.addon;

import org.jspecify.annotations.NullMarked;

/**
 * Exception when invalid addon found
 */
@NullMarked
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
