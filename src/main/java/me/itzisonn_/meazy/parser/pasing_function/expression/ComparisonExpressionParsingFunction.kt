package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.comparison
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction

object ComparisonExpressionParsingFunction : ParsingFunction<Expression>("comparison_expression") {
    override fun Parser.parse(vararg extra: Any?): Expression {
        var left = parse(AdditionExpressionParsingFunction)

        while (current.type in comparison) {
            val operator = consume().value
            val right = parse(AdditionExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}
