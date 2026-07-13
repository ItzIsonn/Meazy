package me.itzisonn_.meazy.util.text

/**
 * Languages registrar
 */
object TranslationLanguages {
    private val languages = mutableSetOf<TranslationLanguage>()
    private var hasInitialized = false

    fun add(language: TranslationLanguage) {
        require(get(language.id) == null) { "Language with id '${language.id}' already exists" }
        languages += language
    }
    fun get(id: String) = languages.find { it.id == id }
    fun getAll() = languages.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "Languages have already been initialized" }
        hasInitialized = true

        add(TranslationLanguage("en", "English"))
        add(TranslationLanguage("ru", "Русский"))
    }
}
