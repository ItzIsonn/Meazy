package me.itzisonn_.meazy.parser;

import lombok.Getter;
import me.itzisonn_.meazy.lexer.Token;
import org.jspecify.annotations.NullMarked;

import java.util.List;

/**
 * Represents parsing context
 * @see Parser
 */
@Getter
@NullMarked
public class ParsingContext {
    private final Parser parser;

    /**
     * @param tokens List of tokens
     */
    public ParsingContext(List<Token> tokens) {
        parser = new Parser(this, tokens);
    }
}
