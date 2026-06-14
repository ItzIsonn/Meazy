package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.addition
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

object AdditionExpressionParsingFunction : AbstractParsingFunction<Expression>("addition_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        var left = parser.parse(MultiplicationExpressionParsingFunction)

        while (parser.current.type in addition) {
            val operator = parser.consume().value
            val right = parser.parse(AdditionExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}
