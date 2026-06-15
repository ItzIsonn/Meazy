package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.and
import me.itzisonn_.meazy.lexer.TokenTypes.or
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction

object LogicalExpressionParsingFunction : ParsingFunction<Expression>("logical_expression") {
    override fun Parser.parse(vararg extra: Any?): Expression {
        var left = parse(ComparisonExpressionParsingFunction)

        var currentType = current.type
        while (currentType == and || currentType == or) {
            val operator = consume().value
            val right = parse(ComparisonExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)

            currentType = current.type
        }

        return left
    }
}
