package me.itzisonn_.meazy.registry;

import java.util.HashSet;
import java.util.Set;

/**
 * Pair-entry registry
 * @see RegistryEntry
 * @see RegistryIdentifier
 *
 * @param <K> Keys' value type
 * @param <V> Values' value type
 */
public class PairRegistry<K, V> {
    private final Set<RegistryEntry<Pair<K, V>>> entries = new HashSet<>();

    /**
     * Registers new entry
     *
     * @param id Identifier to register
     * @param key Pair's key to register
     * @param value Pair's value to register
     * @return Given value
     */
    public V register(RegistryIdentifier id, K key, V value) {
        return register(id, key, value, true);
    }

    /**
     * Registers new entry
     *
     * @param id Identifier to register
     * @param key Pair's key to register
     * @param value Pair's value to register
     * @param overridable Is this entry overridable
     * @return Given value
     */
    public V register(RegistryIdentifier id, K key, V value, boolean overridable) {
        RegistryEntry<Pair<K, V>> entry = getEntry(id);
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with identifier " + id + " already exists!");
        entry = getEntry(key);
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with key " + key + " already exists!");

        entries.add(new RegistryEntry<>(id, new Pair<>(key, value), overridable));
        return value;
    }

    /**
     * Returns all registered entries
     *
     * @return All registered entries
     */
    public Set<RegistryEntry<Pair<K, V>>> getEntries() {
        return Set.copyOf(entries);
    }

    /**
     * Finds entry by given identifier
     *
     * @param id Entry's identifier
     * @return Entry with given identifier
     */
    public RegistryEntry<Pair<K, V>> getEntry(RegistryIdentifier id) {
        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getId().equals(id)) return entry;
        }

        return null;
    }

    /**
     * Finds entry by given key
     *
     * @param key Entry's pair's key
     * @return Entry with pair containing given key
     */
    public RegistryEntry<Pair<K, V>> getEntry(K key) {
        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getValue().getKey().equals(key)) return entry;
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