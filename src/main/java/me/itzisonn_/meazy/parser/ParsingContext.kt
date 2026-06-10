package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.lexer.Token

/**
 * Represents parsing context
 *
 * @param tokens List of tokens
 * 
 * @see Parser
 */
class ParsingContext(tokens: List<Token>) {
    val parser = Parser(this, tokens)
}
