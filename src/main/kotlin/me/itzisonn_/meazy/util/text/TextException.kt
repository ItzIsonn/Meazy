package me.itzisonn_.meazy.util.text

/**
 * Exception that accepts [Text] instead of [String]
 *
 * @param text Text
 */
abstract class TextException(
    text: Text,
    e: Throwable? = null
) : RuntimeException(text.toString(), e)
