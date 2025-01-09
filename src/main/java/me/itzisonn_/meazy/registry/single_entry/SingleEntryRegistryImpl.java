package me.itzisonn_.meazy.registry.single_entry;

import lombok.Getter;
import me.itzisonn_.meazy.registry.RegistryEntry;
import me.itzisonn_.meazy.registry.RegistryIdentifier;

/**
 * Implementation of {@link SingleEntryRegistry}
 *
 * @param <T> Entry's type
 */
@Getter
public class SingleEntryRegistryImpl<T> implements SingleEntryRegistry<T> {
    private RegistryEntry<T> entry;

    @Override
    public void register(RegistryIdentifier identifier, T value) {
        register(identifier, value, true);
    }

    @Override
    public void register(RegistryIdentifier identifier, T value, boolean overridable) throws IllegalArgumentException {
        if (hasEntry() && !entry.isOverrideable()) throw new IllegalArgumentException("Registry already has a value!");
        entry = new RegistryEntry<>(identifier, value, overridable);
    }
}