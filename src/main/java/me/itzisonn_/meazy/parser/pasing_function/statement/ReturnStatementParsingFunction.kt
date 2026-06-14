package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.`return`
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class ReturnStatementParsingFunction : AbstractParsingFunction<ReturnStatement>("return_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): ReturnStatement {
        val parser = context.parser
        parser.next(`return`, translatable("meazy:parser.expected.keyword", "return"))

        var expression: Expression? = null
        if (parser.current.type != newLine) {
            expression = parser.parse<Expression>(getDefaultIdentifier("expression"))
        }

        return ReturnStatement(expression)
    }
}
