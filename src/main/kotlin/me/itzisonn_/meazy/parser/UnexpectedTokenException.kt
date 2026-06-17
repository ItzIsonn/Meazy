package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.TextException
import me.itzisonn_.meazy.text.translatable

/**
 * Is thrown when [me.itzisonn_.meazy.parser.pasing_function.ParsingFunction] meets unexpected token
 *
 * @param lineNumber Line number
 * @param text Text
 */
class UnexpectedTokenException(lineNumber: Int, text: Text) : TextException(
    translatable("meazy:parser.unexpected_token", lineNumber, text)
)
