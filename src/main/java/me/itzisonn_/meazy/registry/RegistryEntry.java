package me.itzisonn_.meazy.registry;

import lombok.Getter;

/**
 * Entry used by registries
 *
 * @see Registry
 * @see RegistryIdentifier
 *
 * @param <T> Value's type
 */
@Getter
public class RegistryEntry<T> {
    /**
     * Entry's id
     */
    private final RegistryIdentifier identifier;
    /**
     * Entry's value
     */
    private final T value;
    /**
     * Is this entry overridable
     */
    private final boolean overrideable;

    /**
     * RegistryEntry constructor
     *
     * @param identifier Entry's id
     * @param value Entry's value
     * @param overrideable Is this entry overridable
     *
     * @throws NullPointerException If given identifier or value is null
     */
    public RegistryEntry(RegistryIdentifier identifier, T value, boolean overrideable) throws NullPointerException {
        if (identifier == null) throw new NullPointerException("Identifier can't be null");
        if (value == null) throw new NullPointerException("Value can't be null");

        this.identifier = identifier;
        this.value = value;
        this.overrideable = overrideable;
    }

    /**
     * RegistryEntry constructor with overridable param defaulted to true
     *
     * @param identifier Entry's id
     * @param value Entry's value
     *
     * @throws NullPointerException If given identifier or value is null
     */
    public RegistryEntry(RegistryIdentifier identifier, T value) {
        this(identifier, value, true);
    }
}