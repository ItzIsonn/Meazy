package me.itzisonn_.meazy.addon;

/**
 * Exception thrown when enabling Addon
 */
public class AddonEnableException extends Exception {
    public AddonEnableException(String message) {
        super(message);
    }

    public AddonEnableException(Throwable cause) {
        super(cause);
    }

    public AddonEnableException(String message, Throwable cause) {
        super(message, cause);
    }
}