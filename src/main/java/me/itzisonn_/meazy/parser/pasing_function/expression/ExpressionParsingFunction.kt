package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction

object ExpressionParsingFunction : ParsingFunction<Expression>("expression") {
    override fun Parser.parse(vararg extra: Any?): Expression {
        return parse(ListCreationExpressionParsingFunction)
    }
}
