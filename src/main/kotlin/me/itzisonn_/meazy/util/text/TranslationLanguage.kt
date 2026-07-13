package me.itzisonn_.meazy.util.text

/**
 * Represents language
 *
 * @param id   Id
 * @param name Name
 * @throws IllegalArgumentException If given id is invalid
 */
class TranslationLanguage(val id: String, val name: String) {
    init {
        require(id.matches("[a-zA-Z_]+".toRegex())) { "Invalid id" }
    }
}
