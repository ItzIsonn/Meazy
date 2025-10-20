package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.lexer.Token;

import java.util.List;

/**
 * Represents function that is used to tokenize lines
 * @see Registries#TOKENIZATION_FUNCTION
 * @see Token
 */
public interface TokenizationFunction {
    /**
     * Tokenizes given string
     * @param string String to tokenize
     * @return List of resulted tokens
     */
    List<Token> tokenize(String string);
}
