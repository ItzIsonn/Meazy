package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.lexer.TokenTypes.inversion
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

class InversionExpressionParsingFunction : AbstractParsingFunction<Expression>("inversion_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser

        if (parser.current.type == inversion) {
            parser.next()
            val expression = parser.parseAfter<Expression>(getDefaultIdentifier("inversion_expression"))
            return OperatorExpression(expression, null, Operators.inversion)
        }

        return parser.parseAfter<Expression>(getDefaultIdentifier("inversion_expression"))
    }
}
