package me.itzisonn_.meazy.lexer

import me.itzisonn_.meazy.registry.Registries

/**
 * Represents function that is used to tokenize lines
 *
 * @see Registries.TOKENIZATION_FUNCTION
 * @see Token
 */
fun interface TokenizationFunction {
    /**
     * Tokenizes given string
     * @param string String to tokenize
     * @return List of resulted tokens
     */
    fun tokenize(string: String): List<Token>
}
