package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.`return`
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ReturnStatementParsingFunction : ParsingFunction<ReturnStatement> {
    override fun Parser.parse(vararg extra: Any?): ReturnStatement {
        consume(`return`, translatable("meazy:parser.expected.keyword", "return"))

        var expression: Expression? = null
        if (current.type != newLine) {
            expression = parse(ExpressionParsingFunction)
        }

        return ReturnStatement(expression)
    }
}
