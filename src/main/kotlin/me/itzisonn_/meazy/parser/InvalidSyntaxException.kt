package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.util.text.Text
import me.itzisonn_.meazy.util.text.TextException
import me.itzisonn_.meazy.util.text.translatable

class InvalidSyntaxException(lineNumber: Int, text: Text) : TextException(
    translatable("meazy:parser.exception.invalid_syntax", lineNumber, text)
)
