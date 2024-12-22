package me.itzisonn_.meazy.registry;

import lombok.Getter;

/**
 * Entry used by registries
 * @see RegistryIdentifier
 * @see SingleRegistry
 * @see SetRegistry
 * @see PairRegistry
 *
 * @param <T> Value's type
 */
@Getter
public class RegistryEntry<T> {
    /**
     * Entry's id
     */
    private final RegistryIdentifier id;
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
     * @param id Entry's id
     * @param value Entry's value
     * @param overrideable Is this entry overridable
     */
    public RegistryEntry(RegistryIdentifier id, T value, boolean overrideable) {
        this.id = id;
        this.value = value;
        this.overrideable = overrideable;
    }

    /**
     * RegistryEntry constructor
     *
     * @param id Entry's id
     * @param value Entry's value
     */
    public RegistryEntry(RegistryIdentifier id, T value) {
        this.id = id;
        this.value = value;
        this.overrideable = true;
    }
}