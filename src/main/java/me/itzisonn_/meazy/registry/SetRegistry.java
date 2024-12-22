package me.itzisonn_.meazy.registry;

import java.util.*;

/**
 * Multiple-entry registry
 * @see RegistryEntry
 * @see RegistryIdentifier
 *
 * @param <T> Entries' value type
 */
public class SetRegistry<T> {
    private final Set<RegistryEntry<T>> entries = new HashSet<>();

    /**
     * Registers new entry
     *
     * @param id Identifier to register
     * @param value Value to register
     * @return Given value
     */
    public T register(RegistryIdentifier id, T value) {
        return register(id, value, true);
    }

    /**
     * Registers new entry
     *
     * @param id Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     * @return Given value
     */
    public T register(RegistryIdentifier id, T value, boolean overridable) {
        RegistryEntry<T> entry = getEntry(id);
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with identifier " + id + " already exists!");
        entries.add(new RegistryEntry<>(id, value, overridable));
        return value;
    }

    /**
     * Returns all registered entries
     *
     * @return All registered entries
     */
    public Set<RegistryEntry<T>> getEntries() {
        return Set.copyOf(entries);
    }

    /**
     * Finds entry by given identifier
     *
     * @param id Entry's identifier
     * @return Entry with given identifier
     */
    public RegistryEntry<T> getEntry(RegistryIdentifier id) {
        for (RegistryEntry<T> entry : entries) {
            if (entry.getId().equals(id)) return entry;
        }

        return null;
    }

    /**
     * Finds entry by given value
     *
     * @param value Entry's value
     * @return Entry with given value
     */
    public RegistryEntry<T> getEntry(T value) {
        for (RegistryEntry<T> entry : entries) {
            if (entry.getValue().equals(value)) return entry;
        }

        return null;
    }

    /**
     * @param id Entry's id
     * @return Whether registered entry with given id
     */
    public boolean hasEntry(RegistryIdentifier id) {
        return getEntry(id) != null;
    }
}