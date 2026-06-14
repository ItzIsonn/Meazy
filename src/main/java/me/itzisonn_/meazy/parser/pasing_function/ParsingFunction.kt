package me.itzisonn_.meazy.parser.pasing_function

import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.lexer.Token

/**
 * Represents function that is used to parse [Token]s into [ProgramUnit]
 *
 * @param T Type of ProgramUnit to parse into
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



abstract class AbstractParsingFunction<T : ProgramUnit>(val id: String) : ParsingFunction<T>