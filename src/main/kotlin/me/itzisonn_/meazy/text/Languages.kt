package me.itzisonn_.meazy.text

/**
 * Languages registrar
 */
object Languages {
    private val languages = mutableSetOf<Language>()
    private var hasInitialized = false

    fun add(language: Language) { languages += language }
    fun get(id: String) = languages.find { it.id == id }
    fun getAll() = languages.toSet()

    internal fun initialize() {
        check(!hasInitialized) { "Languages have already been initialized" }
        hasInitialized = true

        add(Language("en", "English"))
        add(Language("ru", "Русский"))
    }
}
