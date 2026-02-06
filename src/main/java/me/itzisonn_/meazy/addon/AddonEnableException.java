package me.itzisonn_.meazy.addon;

import org.jspecify.annotations.NullMarked;

/**
 * Exception thrown when enabling Addon
 */
@NullMarked
public class AddonEnableException extends Exception {
    public AddonEnableException(String message) {
        super(message);
    }
}