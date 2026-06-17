package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable

object BreakStatementParsingFunction : ParsingFunction<BreakStatement> {
    override fun Parser.parse(vararg extra: Any?): BreakStatement {
        next(`break`, translatable("meazy:parser.expected.keyword", "break"))
        return BreakStatement()
    }
}
