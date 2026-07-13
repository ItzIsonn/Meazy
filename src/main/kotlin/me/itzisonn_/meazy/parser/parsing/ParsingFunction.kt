package me.itzisonn_.meazy.parser.parsing

import me.itzisonn_.meazy.parser.ast.ProgramUnit
import me.itzisonn_.meazy.lexer.Token

/**
 * Represents function that is used to parse [Token]s into [ProgramUnit]
 *
 * @param T Type of ProgramUnit to parse into
 */
interface ParsingFunction<T : ProgramUnit, D> {
    /**
     * Parses tokens, which are stored in [Parser], into ProgramUnit of type [T]
     *
     * @receiver Parser
     * @param data Extra data for parsing
     *
     * @return Parsed ProgramUnit of type [T]
     */
    fun Parser.parse(data: D): T
}

abstract class EmptyParsingFunction<T : ProgramUnit> : ParsingFunction<T, Unit> {
    abstract fun Parser.parse(): T
    final override fun Parser.parse(data: Unit) = parse()
}

abstract class PairParsingFunction<T : ProgramUnit, A, B> : ParsingFunction<T, Pair<A, B>> {
    abstract fun Parser.parse(first: A, second: B): T
    final override fun Parser.parse(data: Pair<A, B>) = parse(data.first, data.second)
}