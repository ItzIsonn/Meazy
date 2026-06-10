package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.registry.Registries
import me.itzisonn_.meazy.text.TextException
import me.itzisonn_.meazy.text.translatable

/**
 * Is thrown when [Registries.TOKENIZATION_FUNCTION] can't recognize token
 *
 * @param lineNumber Line number
 * @param errorString Error string
 */
class UnknownTokenException(lineNumber: Int, errorString: String) : TextException(
    translatable("meazy:lexer.unknown_token", lineNumber, errorString)
)