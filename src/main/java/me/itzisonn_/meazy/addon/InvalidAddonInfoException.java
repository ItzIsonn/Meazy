package me.itzisonn_.meazy.addon;

/**
 * Is thrown when invalid addon.json found
 */
public class InvalidAddonInfoException extends Exception {
    public InvalidAddonInfoException(Throwable cause) {
        super("Invalid addon.json", cause);
    }
}