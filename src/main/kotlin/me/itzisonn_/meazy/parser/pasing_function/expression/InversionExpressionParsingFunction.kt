package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.lexer.TokenTypes.inversion
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction

object InversionExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        if (isNext(inversion)) {
            consume(inversion, null)
            val expression = parse(IsExpressionParsingFunction)
            return OperatorExpression(expression, null, Operators.inversion)
        }

        return parse(IsExpressionParsingFunction)
    }
}
