package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.text.Text
import me.itzisonn_.meazy.text.TextException
import me.itzisonn_.meazy.text.translatable

class InvalidStatementException(lineNumber: Int, text: Text) : TextException(
    translatable("meazy:parser.exception.invalid_statement", lineNumber, text)
)
