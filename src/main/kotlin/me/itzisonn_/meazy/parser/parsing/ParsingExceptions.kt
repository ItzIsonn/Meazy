package me.itzisonn_.meazy.parser.parsing

import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.util.text.Text
import me.itzisonn_.meazy.util.text.TextException
import me.itzisonn_.meazy.util.text.literal
import me.itzisonn_.meazy.util.text.translatable

sealed class ParsingException(text: Text) : TextException(text)

class UnexpectedTokenException(token: Token, text: Text?) : ParsingException(
    translatable("parser.unexpected_token", token.type.id, token.line).run {
        if (text != null) {
            return@run append(literal(": ")).append(text)
        }

        return@run this
    }
)

class InvalidStatementException(lineNumber: Int, text: Text) : ParsingException(
    translatable("parser.exception.invalid_statement", lineNumber, text)
)

class InvalidSyntaxException(lineNumber: Int, text: Text) : ParsingException(
    translatable("parser.exception.invalid_syntax", lineNumber, text)
)