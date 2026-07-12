package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.util.text.Text
import me.itzisonn_.meazy.util.text.TextException
import me.itzisonn_.meazy.util.text.literal
import me.itzisonn_.meazy.util.text.translatable
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction

/**
 * Is thrown when [ParsingFunction] meets unexpected token
 *
 * @param token Token
 * @param text Text
 */
class UnexpectedTokenException(token: Token, text: Text?) : TextException(
    translatable("meazy:parser.unexpected_token", token.type.id, token.line).run {
        if (text != null) {
            return@run append(literal(": ")).append(text)
        }

        return@run this
    }
)
