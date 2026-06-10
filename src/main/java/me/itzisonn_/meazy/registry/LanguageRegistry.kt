package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.text.Language
import me.itzisonn_.registry.RegistryEntry
import me.itzisonn_.registry.RegistryIdentifier
import me.itzisonn_.registry.multiple_entry.SetRegistry

class LanguageRegistry : SetRegistry<Language>() {
    override fun register(identifier: RegistryIdentifier, value: Language, overridable: Boolean) {
        if (getEntry(value.id) != null) return
        super.register(identifier, value, overridable)
    }

    /**
     * Finds an entry by language with given id
     * 
     * @param id Language's id
     * @return Entry with language with given id
     */
    fun getEntry(id: String): RegistryEntry<Language>? {
        for (entry in entries) {
            if (entry.value.id == id) {
                return entry
            }
        }

        return null
    }
}
