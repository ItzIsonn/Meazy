package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.minus
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operators.negation
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

object NegationExpressionParsingFunction : AbstractParsingFunction<Expression>("negation_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser

        if (parser.current.type == minus) {
            parser.next()
            val expression = parser.parse(MemberExpressionParsingFunction)
            return OperatorExpression(expression, null, negation)
        }

        return parser.parse(MemberExpressionParsingFunction)
    }
}
