package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`continue`
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.ContinueStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

object ContinueStatementParsingFunction : AbstractParsingFunction<ContinueStatement>("continue_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): ContinueStatement {
        context.parser.next(`continue`, translatable("meazy:parser.expected.keyword", "continue"))
        return ContinueStatement()
    }
}
