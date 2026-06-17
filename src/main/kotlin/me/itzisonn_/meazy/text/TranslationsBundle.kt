package me.itzisonn_.meazy.text

import kotlinx.serialization.json.Json
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.logger.LogLevel
import me.itzisonn_.meazy.logger.Logger

/**
 * Represents translations bundle
 */
object TranslationsBundle {
    private var language = Registries.LANGUAGES.getEntry("en")?.value!!
    private val translations = mutableMapOf<String, String>()

    init {
        updateTranslations()
    }



    /**
     * Updates language
     *
     * @param language New language
     */
    fun setLanguage(language: Language) {
        this.language = language
        updateTranslations()
    }

    /**
     * Updates translations
     */
    private fun updateTranslations() {
        translations.clear()

        val text = MeazyMain::class.java.classLoader.getResource("lang/${language.id}.json")
            ?.readText()
            ?: error("Failed to update translations")

        translations.putAll(Json.decodeFromString<Map<String, String>>(text))

        translations.keys.removeIf { key ->
            val remove = !key.matches("[a-zA-Z_.]+".toRegex())
            if (remove) Logger.log(
                LogLevel.WARNING,
                literal(
                    "Key with id {0} was removed from translation bundle because of invalid format",
                    key
                )
            )
            remove
        }
    }



    /**
     * @param key Translation key
     * @return Translation that corresponds to given key
     */
    fun getTranslation(key: String): String? {
        return translations[key]
    }

    /**
     * @param key Translation key
     * @param fallback Translation fallback
     * @return Translation that corresponds to given key or given fallback if it's null
     */
    fun getTranslationOrDefault(key: String, fallback: String): String {
        return translations.getOrDefault(key, fallback)
    }
}