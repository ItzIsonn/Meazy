package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.lang.Language;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import me.itzisonn_.registry.multiple_entry.SetRegistry;

public class LanguageRegistry extends SetRegistry<Language> {
    @Override
    public void register(RegistryIdentifier identifier, Language value, boolean overridable) {
        if (getEntry(value.getId()) != null) return;
        super.register(identifier, value, overridable);
    }

    /**
     * Finds an entry by language with given id
     *
     * @param id Language's id
     * @return Entry with language with given id
     *
     * @throws NullPointerException If given id is null
     */
    public RegistryEntry<Language> getEntry(String id) {
        if (id == null) throw new NullPointerException("Id can't be null");

        for (RegistryEntry<Language> entry : getEntries()) {
            if (entry.getValue().getId().equals(id)) {
                return entry;
            }
        }

        return null;
    }
}
