package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.BREAK
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.BreakStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class BreakStatementParsingFunction : AbstractParsingFunction<BreakStatement>("break_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): BreakStatement {
        context.parser.next(BREAK(), translatable("meazy:parser.expected.keyword", "break"))
        return BreakStatement()
    }
}
