package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.context.ParsingContext;
import me.itzisonn_.meazy.parser.ast.Statement;
import me.itzisonn_.meazy.Registries;
import me.itzisonn_.meazy.lexer.Token;

/**
 * Represents function that is used to parse {@link Token}s into {@link Statement}
 *
 * @param <T> Type of Statement to parse into
 * @see Registries#PARSING_FUNCTIONS
 */
public interface ParsingFunction<T extends Statement> {
    /**
     * Parses tokens, which is stored in {@link Parser}, into {@link T}
     *
     * @param context Parsing context
     * @param extra Extra info
     * @return Parsed {@link T} Statement
     */
    T parse(ParsingContext context, Object... extra);
}