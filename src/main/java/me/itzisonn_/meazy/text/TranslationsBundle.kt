package me.itzisonn_.meazy.text

import kotlinx.serialization.json.Json
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.util.FileUtils
import me.itzisonn_.meazy.util.logger.LogLevel
import me.itzisonn_.meazy.util.logger.Logger
import java.io.IOException

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

        try {
            MeazyMain::class.java.classLoader.getResourceAsStream("lang/" + language.id + ".json").use { inputStream ->
                if (inputStream != null) translations.putAll(
                    Json.decodeFromString<Map<String, String>>(FileUtils.getLines(inputStream))
                )
            }
        }
        catch (e: IOException) {
            throw RuntimeException("Failed to update translations", e)
        }

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