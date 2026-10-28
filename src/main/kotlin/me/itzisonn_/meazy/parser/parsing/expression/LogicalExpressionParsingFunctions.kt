package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.or
import me.itzisonn_.meazy.lexer.TokenTypes.and
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction

object OrExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        var left = parse(AndExpressionParsingFunction)

        while (isNext(or)) {
            val operator = consume(or, null).value
            val right = parse(AndExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}

object AndExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        var left = parse(ComparisonExpressionParsingFunction)

        while (isNext(and)) {
            val operator = consume(and, null).value
            val right = parse(ComparisonExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}

