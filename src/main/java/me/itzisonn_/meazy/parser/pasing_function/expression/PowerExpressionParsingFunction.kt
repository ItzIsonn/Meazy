package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.power
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

object PowerExpressionParsingFunction : AbstractParsingFunction<Expression>("power_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        var left = parser.parseAfter<Expression>(getDefaultIdentifier("power_expression"))

        while (parser.current.type == power) {
            parser.next()
            val right = parser.parseAfter<Expression>(getDefaultIdentifier("power_expression"))
            left = OperatorExpression(left, right, Operators.power)
        }

        return left
    }
}
