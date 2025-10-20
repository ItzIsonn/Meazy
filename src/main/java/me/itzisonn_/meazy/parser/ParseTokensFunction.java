package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.Program;

import java.io.File;
import java.util.List;

/**
 * Represents function that is used to parse {@link Token}s
 * @see Registries#PARSE_TOKENS_FUNCTION
 */
public interface ParseTokensFunction {
    /**
     * Parses given tokens into a Program
     *
     * @param file File
     * @param tokens List of tokens
     *
     * @return Parsed program
     */
    Program parse(File file, List<Token> tokens);
}
