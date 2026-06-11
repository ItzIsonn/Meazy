package me.itzisonn_.meazy.parser;

import me.itzisonn_.meazy.parser.ast.ProgramUnit;
import me.itzisonn_.meazy.registry.Registries;
import me.itzisonn_.meazy.lexer.Token;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Represents function that is used to parse {@link Token}s into {@link ProgramUnit}
 *
 * @param <T> Type of ProgramUnit to parse into
 * @see Registries#PARSING_FUNCTIONS
 */
@FunctionalInterface
@NullMarked
public interface ParsingFunction<T extends ProgramUnit> {
    /**
     * Parses tokens, which is stored in {@link Parser}, into {@link T}
     *
     * @param context Parsing context
     * @param extra Extra info
     * @return Parsed {@link T} ProgramUnit
     */
    T parse(ParsingContext context, @Nullable Object... extra);
}