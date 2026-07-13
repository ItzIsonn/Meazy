package me.itzisonn_.meazy.parser.parsing

import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.lexer.Token

/**
 * Represents function that is used to parse [Token]s into [ProgramUnit]
 *
 * @param T Type of ProgramUnit to parse into
 */
interface ParsingFunction<T : ProgramUnit> {
    /**
     * Parses tokens, which is stored in [Parser], into program unit of type [T]
     *
     * @receiver Parser
     * @param extra Extra info
     *
     * @return Parsed [T] ProgramUnit
     */
    fun Parser.parse(vararg extra: Any?): T
}