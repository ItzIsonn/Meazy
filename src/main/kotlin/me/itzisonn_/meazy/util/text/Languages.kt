package me.itzisonn_.meazy.util.text

/**
 * Languages registrar
 */
object Languages {
    private val languages = mutableSetOf<Language>()
    private var hasInitialized = false

    fun add(language: Language) {
        require(get(language.id) == null) { "Language with id '${language.id}' already exists" }
        languages += language
    }
    fun get(id: String) = languages.find { it.id == id }
    fun getAll() = languages.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "Languages have already been initialized" }
        hasInitialized = true

        add(Language("en", "English"))
        add(Language("ru", "Русский"))
    }
}
