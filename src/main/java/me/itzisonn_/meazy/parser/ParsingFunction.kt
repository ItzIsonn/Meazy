package me.itzisonn_.meazy.parser

import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.registry.Registries

/**
 * Represents function that is used to parse [Token]s into [ProgramUnit]
 * 
 * @param T Type of ProgramUnit to parse into
 * @see Registries.PARSING_FUNCTIONS
 */
fun interface ParsingFunction<T : ProgramUnit> {
    /**
     * Parses tokens, which is stored in [Parser], into [T]
     * 
     * @param context Parsing context
     * @param extra Extra info
     *
     * @return Parsed [T] ProgramUnit
     */
    fun parse(context: ParsingContext, vararg extra: Any?): T
}