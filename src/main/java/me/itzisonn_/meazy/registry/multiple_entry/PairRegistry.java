package me.itzisonn_.meazy.registry.multiple_entry;

import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of {@link MultipleEntryRegistry} with entries stored in {@link Set} of {@link Pair}. Key must be unique value
 *
 * @param <K> Keys' type
 * @param <V> Values' type
 */
public class PairRegistry<K, V> implements MultipleEntryRegistry<Pair<K, V>> {
    private final Set<RegistryEntry<Pair<K, V>>> entries = new HashSet<>();

    @Override
    public void register(RegistryIdentifier identifier, Pair<K, V> value, boolean overridable) throws IllegalArgumentException {
        RegistryEntry<Pair<K, V>> entry = getEntry(identifier);
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with identifier " + identifier + " already exists!");
        entry = getEntryByKey(value.getKey());
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with key " + value.getKey() + " already exists!");

        entries.add(new RegistryEntry<>(identifier, value, overridable));
    }

    /**
     * Registers new entry
     *
     * @param identifier Identifier to register
     * @param key Pair's key to register
     * @param value Pair's value to register
     *
     * @throws IllegalArgumentException When not overridable entry is already registered
     */
    public void register(RegistryIdentifier identifier, K key, V value) throws IllegalArgumentException {
        register(identifier, key, value, true);
    }

    /**
     * Registers new entry
     *
     * @param identifier Identifier to register
     * @param key Pair's key to register
     * @param value Pair's value to register
     * @param overridable Is this entry overridable
     *
     * @throws IllegalArgumentException When not overridable entry is already registered
     */
    public void register(RegistryIdentifier identifier, K key, V value, boolean overridable) throws IllegalArgumentException {
        register(identifier, new Pair<>(key, value), overridable);
    }

    @Override
    public Set<RegistryEntry<Pair<K, V>>> getEntries() {
        return Set.copyOf(entries);
    }

    @Override
    public RegistryEntry<Pair<K, V>> getEntry(RegistryIdentifier id) {
        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getIdentifier().equals(id)) return entry;
        }

        return null;
    }

    @Override
    public RegistryEntry<Pair<K, V>> getEntry(Pair<K, V> value) {
        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getValue().equals(value)) return entry;
        }

        return null;
    }

    /**
     * Finds entry by given key
     *
     * @param key Entry's pair's key
     * @return Entry with pair containing given key
     */
    public RegistryEntry<Pair<K, V>> getEntryByKey(K key) {
        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getValue().getKey().equals(key)) return entry;
        }

        return null;
    }
}