package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.and
import me.itzisonn_.meazy.lexer.TokenTypes.or
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

object LogicalExpressionParsingFunction : AbstractParsingFunction<Expression>("logical_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        var left = parser.parseAfter<Expression>(getDefaultIdentifier("logical_expression"))

        var current = parser.current.type
        while (current == and || current == or) {
            val operator = parser.consume().value
            val right = parser.parseAfter<Expression>(getDefaultIdentifier("logical_expression"))
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)

            current = parser.current.type
        }

        return left
    }
}
