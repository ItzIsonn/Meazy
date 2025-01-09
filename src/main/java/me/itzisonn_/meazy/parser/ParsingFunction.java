package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.parser.ast.statement.Statement;
import me.itzisonn_.meazy.registry.Registries;

/**
 * ParsingFunction
 *
 * @param <T> Type of Statement to parse
 *
 * @see Registries#PARSING_FUNCTIONS
 */
public interface ParsingFunction<T extends Statement> {
    /**
     * Parses tokens in {@link Parser} to {@link T}
     *
     * @param extra Extra info
     * @return Parsed {@link T} Statement
     */
    T parse(Object... extra);
}