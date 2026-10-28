package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.doubleQuestion
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.NullCheckExpression
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction

object NullCheckExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        val checkExpression = parse(OrExpressionParsingFunction)
        if (!isNext(doubleQuestion)) return checkExpression
        consume(doubleQuestion, null)

        val nullExpression = parse(ExpressionParsingFunction)
        return NullCheckExpression(checkExpression, nullExpression)
    }
}
