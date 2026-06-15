package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`continue`
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.ContinueStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable

object ContinueStatementParsingFunction : ParsingFunction<ContinueStatement> {
    override fun Parser.parse(vararg extra: Any?): ContinueStatement {
        next(`continue`, translatable("meazy:parser.expected.keyword", "continue"))
        return ContinueStatement()
    }
}
