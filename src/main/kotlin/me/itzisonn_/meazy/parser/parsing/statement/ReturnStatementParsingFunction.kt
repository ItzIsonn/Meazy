package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.`return`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ReturnStatementParsingFunction : EmptyParsingFunction<ReturnStatement>() {
    override fun Parser.parse(): ReturnStatement {
        consume(`return`, translatable("parser.expected.keyword", "return"))

        var expression: Expression? = null
        if (!isNext(newLine)) {
            expression = parse(ExpressionParsingFunction)
        }

        return ReturnStatement(expression)
    }
}
