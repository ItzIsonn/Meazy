package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.parsing.ParsingFunction

object ExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        return parse(ListCreationExpressionParsingFunction)
    }
}
