package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.questionColon
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.NullCheckExpression
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction

object NullCheckExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        val checkExpression = parse(LogicalExpressionParsingFunction)

        if (isNext(questionColon)) {
            consume(questionColon, null)
            val nullExpression = parse(ExpressionParsingFunction)
            return NullCheckExpression(checkExpression, nullExpression)
        }

        return checkExpression
    }
}
