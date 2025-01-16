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

    /**
     * Registers new entry
     *
     * @param identifier Identifier to register
     * @param value Value to register
     * @param overridable Is this entry overridable
     *
     * @throws NullPointerException If given identifier or value is null
     * @throws IllegalArgumentException When not overridable entry with given identifier or key of pair is already registered
     */
    @Override
    public void register(RegistryIdentifier identifier, Pair<K, V> value, boolean overridable) throws NullPointerException, IllegalArgumentException {
        if (identifier == null) throw new NullPointerException("Identifier can't be null");
        if (value == null) throw new NullPointerException("Value can't be null");

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
     * @throws NullPointerException If given identifier or key is null
     * @throws IllegalArgumentException When not overridable entry with given identifier or key is already registered
     */
    public void register(RegistryIdentifier identifier, K key, V value) throws NullPointerException, IllegalArgumentException {
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
     * @throws NullPointerException If given identifier or key is null
     * @throws IllegalArgumentException When not overridable entry with given identifier or key is already registered
     */
    public void register(RegistryIdentifier identifier, K key, V value, boolean overridable) throws NullPointerException, IllegalArgumentException {
        register(identifier, new Pair<>(key, value), overridable);
    }

    @Override
    public Set<RegistryEntry<Pair<K, V>>> getEntries() {
        return Set.copyOf(entries);
    }

    @Override
    public RegistryEntry<Pair<K, V>> getEntry(RegistryIdentifier identifier) throws NullPointerException {
        if (identifier == null) throw new NullPointerException("Identifier can't be null");

        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getIdentifier().equals(identifier)) return entry;
        }

        return null;
    }

    @Override
    public RegistryEntry<Pair<K, V>> getEntry(Pair<K, V> value) throws NullPointerException {
        if (value == null) throw new NullPointerException("Value can't be null");

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
     *
     * @throws NullPointerException If given key is null
     */
    public RegistryEntry<Pair<K, V>> getEntryByKey(K key) throws NullPointerException {
        if (key == null) throw new NullPointerException("Key can't be null");

        for (RegistryEntry<Pair<K, V>> entry : entries) {
            if (entry.getValue().getKey().equals(key)) return entry;
        }

        return null;
    }
}