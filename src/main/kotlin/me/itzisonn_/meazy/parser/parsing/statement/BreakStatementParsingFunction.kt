package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object BreakStatementParsingFunction : EmptyParsingFunction<BreakStatement>() {
    override fun Parser.parse(): BreakStatement {
        consume(`break`, translatable("parser.expected.keyword", "break"))
        return BreakStatement()
    }
}
