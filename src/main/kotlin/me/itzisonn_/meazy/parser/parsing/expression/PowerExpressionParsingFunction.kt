package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.power
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.parsing.ParsingFunction

object PowerExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        var left = parse(InversionExpressionParsingFunction)

        while (isNext(power)) {
            consume(power, null)
            val right = parse(InversionExpressionParsingFunction)
            left = OperatorExpression(left, right, Operators.power)
        }

        return left
    }
}
