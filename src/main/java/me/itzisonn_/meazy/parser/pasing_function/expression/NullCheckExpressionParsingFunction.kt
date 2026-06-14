package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.questionColon
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.NullCheckExpression
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

object NullCheckExpressionParsingFunction : AbstractParsingFunction<Expression>("null_check_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        val checkExpression = parser.parse(LogicalExpressionParsingFunction)

        if (parser.current.type == questionColon) {
            parser.next()
            val nullExpression = parser.parse(ExpressionParsingFunction)
            return NullCheckExpression(checkExpression, nullExpression)
        }

        return checkExpression
    }
}
