package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypeSets.multiplication
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

object MultiplicationExpressionParsingFunction : AbstractParsingFunction<Expression>("multiplication_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        var left = parser.parseAfter<Expression>(getDefaultIdentifier("multiplication_expression"))

        while (parser.current.type in multiplication) {
            val operator = parser.consume().value
            val right = parser.parseAfter<Expression>(getDefaultIdentifier("multiplication_expression"))
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}
