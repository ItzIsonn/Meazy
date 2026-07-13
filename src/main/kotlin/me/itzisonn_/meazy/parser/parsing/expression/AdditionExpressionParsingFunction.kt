package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.addition
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.parsing.ParsingFunction

object AdditionExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        var left = parse(MultiplicationExpressionParsingFunction)

        while (isNext(addition)) {
            val operator = consume(addition, null).value
            val right = parse(AdditionExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}
