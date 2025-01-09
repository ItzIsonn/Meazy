package me.itzisonn_.meazy.registry;

/**
 * Registry
 *
 * @see Registries
 * @see RegistryEntry
 * @see RegistryIdentifier
 *
 * @param <T> Entry's type
 */
public interface Registry<T> {
    /**
     * Registers new overridable entry
     *
     * @param identifier Identifier to register
     * @param value Value to register
     *
     * @throws IllegalArgumentException When not overridable entry is already registered
     */
    default void register(RegistryIdentifier identifier, T value) throws IllegalArgumentException {
        register(identifier, value, true);
    }

    /**
     * Registers new entry
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     *
     * @throws IllegalArgumentException When not overridable entry is already registered
     */
    void register(RegistryIdentifier identifier, T value, boolean overridable) throws IllegalArgumentException;

    /**
     * Registers new entry in given registry
     *
     * @param registry Which Registry should the entry be registered in
     * @param identifier Identifier to register
     * @param value Value to register
     *
     * @throws IllegalArgumentException When not overridable entry is already registered
     */
    static <T> void register(Registry<T> registry, RegistryIdentifier identifier, T value) throws IllegalArgumentException {
        registry.register(identifier, value);
    }

    /**
     * Registers new entry in given registry
     *
     * @param registry Which Registry should the entry be registered in
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     *
     * @throws IllegalArgumentException When not overridable entry is already registered
     */
    static <T> void register(Registry<T> registry, RegistryIdentifier identifier, T value, boolean overridable) throws IllegalArgumentException {
        registry.register(identifier, value, overridable);
    }
}