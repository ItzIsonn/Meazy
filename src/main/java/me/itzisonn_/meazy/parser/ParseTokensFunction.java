package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.ast.program.Program;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * Represents function that is used to parse {@link Token}s
 * @see Registries#PARSE_TOKENS_FUNCTION
 */
@FunctionalInterface
@NullMarked
public interface ParseTokensFunction {
    /**
     * Parses given tokens into a Program
     *
     * @param file File
     * @param tokens List of tokens
     *
     * @return Parsed program
     */
    Program parse(@Nullable File file, List<Token> tokens);
}
