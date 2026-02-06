package me.itzisonn_.meazy.addon;

import org.jspecify.annotations.NullMarked;

/**
 * Is thrown when invalid addon.json found
 */
@NullMarked
public class InvalidAddonInfoException extends Exception {
    public InvalidAddonInfoException(String message) {
        super("Invalid addon.json: " + message);
    }

    public InvalidAddonInfoException(Throwable cause) {
        super("Invalid addon.json", cause);
    }
}