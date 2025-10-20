package me.itzisonn_.meazy.context;

import lombok.Getter;
import me.itzisonn_.meazy.lexer.Token;
import me.itzisonn_.meazy.parser.Parser;

import java.util.List;

/**
 * Represents parsing context
 * @see Parser
 */
@Getter
public class ParsingContext {
    private final Parser parser;

    /**
     * @param tokens List of tokens
     * @throws NullPointerException If given tokens is null
     */
    public ParsingContext(List<Token> tokens) throws NullPointerException {
        if (tokens == null) throw new NullPointerException("Tokens can't be null");
        parser = new Parser(this, tokens);
    }
}
