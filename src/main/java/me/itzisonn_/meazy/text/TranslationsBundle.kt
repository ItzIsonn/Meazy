package me.itzisonn_.meazy.text

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.util.FileUtils
import me.itzisonn_.meazy.util.logger.LogLevel
import java.io.IOException

/**
 * Represents translations bundle
 */
object TranslationsBundle {
    private var language = run {
        val entry = Registries.LANGUAGES.getEntry("en")
        if (entry != null) entry.getValue()
        else Language("en", "English")
    }

    private val GSON = Gson()

    @Suppress("UNCHECKED_CAST")
    private val MAP_TYPE_TOKEN = TypeToken.getParameterized(
        MutableMap::class.java,
        String::class.java,
        String::class.java
    ) as TypeToken<MutableMap<String, String>>

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
                    GSON.fromJson(FileUtils.getLines(inputStream), MAP_TYPE_TOKEN)
                )
            }
        }
        catch (e: IOException) {
            throw RuntimeException("Failed to update translations", e)
        }

        translations.keys.removeIf { key ->
            val remove = !key.matches("[a-zA-Z_.]+".toRegex())
            if (remove) MeazyMain.LOGGER.log(
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