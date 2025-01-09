package me.itzisonn_.meazy.registry.single_entry;

import me.itzisonn_.meazy.registry.Registry;
import me.itzisonn_.meazy.registry.RegistryEntry;

/**
 * Registry with only one possible entry
 *
 * @param <T> Entry's value type
 */
public interface SingleEntryRegistry<T> extends Registry<T> {
    /**
     * @return Registered entry
     */
    RegistryEntry<T> getEntry();

    /**
     * @return Has registered entry
     */
    default boolean hasEntry() {
        return getEntry() != null;
    }
}
