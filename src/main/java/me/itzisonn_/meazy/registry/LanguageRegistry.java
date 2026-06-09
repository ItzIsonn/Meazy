package me.itzisonn_.meazy.registry;

import me.itzisonn_.meazy.text.Language;
import me.itzisonn_.registry.RegistryEntry;
import me.itzisonn_.registry.RegistryIdentifier;
import me.itzisonn_.registry.multiple_entry.SetRegistry;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
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
     */
    @Nullable
    public RegistryEntry<Language> getEntry(String id) {
        for (RegistryEntry<Language> entry : getEntries()) {
            if (entry.getValue().getId().equals(id)) {
                return entry;
            }
        }

        return null;
    }
}
