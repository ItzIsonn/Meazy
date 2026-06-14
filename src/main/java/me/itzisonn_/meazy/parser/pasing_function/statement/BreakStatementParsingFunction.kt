package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

object BreakStatementParsingFunction : AbstractParsingFunction<BreakStatement>("break_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): BreakStatement {
        context.parser.next(`break`, translatable("meazy:parser.expected.keyword", "break"))
        return BreakStatement()
    }
}
