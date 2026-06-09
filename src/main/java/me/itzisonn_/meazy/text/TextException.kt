package me.itzisonn_.meazy.text

/**
 * Exception that accepts [Text] instead of [String]
 *
 * @param text Text
 */
abstract class TextException @JvmOverloads constructor(
    text: Text,
    e: Throwable? = null
) : RuntimeException(text.toString(), e)
