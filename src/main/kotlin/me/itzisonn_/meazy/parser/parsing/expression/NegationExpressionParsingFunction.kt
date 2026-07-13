package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.minus
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.operator.Operators.negation
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction

object NegationExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        if (isNext(minus)) {
            consume(minus, null)
            val expression = parse(MemberExpressionParsingFunction)
            return OperatorExpression(expression, null, negation)
        }

        return parse(MemberExpressionParsingFunction)
    }
}
