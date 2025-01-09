package me.itzisonn_.meazy.registry.multiple_entry;

import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

import java.util.*;

/**
 * Implementation of {@link MultipleEntryRegistry} with entries stored in {@link Set}
 *
 * @param <T> Entries' type
 */
public class SetRegistry<T> implements MultipleEntryRegistry<T> {
    private final Set<RegistryEntry<T>> entries = new HashSet<>();

    @Override
    public void register(RegistryIdentifier identifier, T value, boolean overridable) throws IllegalArgumentException {
        RegistryEntry<T> entry = getEntry(identifier);
        if (entry != null && !entry.isOverrideable()) throw new IllegalArgumentException("Entry with identifier " + identifier + " already exists!");
        entries.add(new RegistryEntry<>(identifier, value, overridable));
    }

    @Override
    public Set<RegistryEntry<T>> getEntries() {
        return Set.copyOf(entries);
    }

    @Override
    public RegistryEntry<T> getEntry(RegistryIdentifier identifier) {
        for (RegistryEntry<T> entry : entries) {
            if (entry.getIdentifier().equals(identifier)) return entry;
        }

        return null;
    }

    @Override
    public RegistryEntry<T> getEntry(T value) {
        for (RegistryEntry<T> entry : entries) {
            if (entry.getValue().equals(value)) return entry;
        }

        return null;
    }
}