package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.lexer.TokenTypes.inversion
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.runtime.data.operator.Operators
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction

object InversionExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        if (!isNext(inversion)) return parse(TypeCheckExpressionParsingFunction)
        consume(inversion, null)

        val expression = parse(TypeCheckExpressionParsingFunction)
        return OperatorExpression(expression, null, Operators.inversion)
    }
}
