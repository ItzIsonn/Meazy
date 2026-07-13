package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object BreakStatementParsingFunction : ParsingFunction<BreakStatement> {
    override fun Parser.parse(vararg extra: Any?): BreakStatement {
        consume(`break`, translatable("meazy:parser.expected.keyword", "break"))
        return BreakStatement()
    }
}
