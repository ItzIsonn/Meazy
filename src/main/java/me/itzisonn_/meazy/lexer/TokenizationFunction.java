package me.itzisonn_.meazy.lexer;

import me.itzisonn_.meazy.Registries;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Represents function that is used to tokenize lines
 * @see Registries#TOKENIZATION_FUNCTION
 * @see Token
 */
@FunctionalInterface
@NullMarked
public interface TokenizationFunction {
    /**
     * Tokenizes given string
     * @param string String to tokenize
     * @return List of resulted tokens
     */
    List<Token> tokenize(String string);
}
