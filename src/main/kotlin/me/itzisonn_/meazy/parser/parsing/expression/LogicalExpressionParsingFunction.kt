package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.logical
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.parsing.ParsingFunction

object LogicalExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        var left = parse(ComparisonExpressionParsingFunction)

        while (isNext(logical)) {
            val operator = consume(logical, null).value
            val right = parse(ComparisonExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}
