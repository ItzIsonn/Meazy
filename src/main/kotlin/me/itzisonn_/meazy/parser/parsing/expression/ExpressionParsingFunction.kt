package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction

object ExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        return parse(ListCreationExpressionParsingFunction)
    }
}
