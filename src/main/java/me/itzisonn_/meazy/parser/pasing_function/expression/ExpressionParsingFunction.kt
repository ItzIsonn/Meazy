package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction

class ExpressionParsingFunction : AbstractParsingFunction<Expression>("expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        return context.parser.parseAfter<Expression>(getDefaultIdentifier("expression"))
    }
}
