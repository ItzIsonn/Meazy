package me.itzisonn_.meazy.registry.multiple_entry;

import me.itzisonn_.meazy.registry.Registry;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.util.Collection;

/**
 * Registry with many possible entries
 *
 * @param <T> Entries' type
 */
public interface MultipleEntryRegistry<T> extends Registry<T> {
    /**
     * @return All registered entries
     */
    Collection<RegistryEntry<T>> getEntries();

    /**
     * Finds an entry by given identifier
     *
     * @param identifier Entry's identifier
     * @return Entry with given identifier
     *
     * @throws NullPointerException If given identifier is null
     */
    RegistryEntry<T> getEntry(RegistryIdentifier identifier) throws NullPointerException;

    /**
     * Finds an entry by given value
     *
     * @param value Entry's value
     * @return Entry with given value
     *
     * @throws NullPointerException If given value is null
     */
    RegistryEntry<T> getEntry(T value) throws NullPointerException;

    /**
     * @param identifier Entry's identifier
     * @return Whether registered entry with given identifier
     *
     * @throws NullPointerException If given identifier is null
     */
    default boolean hasEntry(RegistryIdentifier identifier) throws NullPointerException {
        return getEntry(identifier) != null;
    }

    /**
     * @param value Entry's value
     * @return Whether registered entry with given value
     *
     * @throws NullPointerException If given value is null
     */
    default boolean hasEntry(T value) throws NullPointerException {
        return getEntry(value) != null;
    }
}
