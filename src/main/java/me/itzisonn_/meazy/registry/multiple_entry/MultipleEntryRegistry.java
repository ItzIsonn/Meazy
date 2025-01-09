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
     */
    RegistryEntry<T> getEntry(RegistryIdentifier identifier);

    /**
     * Finds an entry by given value
     *
     * @param value Entry's value
     * @return Entry with given value
     */
    RegistryEntry<T> getEntry(T value);

    /**
     * @param identifier Entry's identifier
     * @return Whether registered entry with given identifier
     */
    default boolean hasEntry(RegistryIdentifier identifier) {
        return getEntry(identifier) != null;
    }

    /**
     * @param value Entry's value
     * @return Whether registered entry with given value
     */
    default boolean hasEntry(T value) {
        return getEntry(value) != null;
    }
}
