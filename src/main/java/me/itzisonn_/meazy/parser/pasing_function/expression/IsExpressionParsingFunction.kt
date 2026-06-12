package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`is`
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.IsExpression
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class IsExpressionParsingFunction : AbstractParsingFunction<Expression>("is_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        val value = parser.parseAfter<Expression>(getDefaultIdentifier("is_expression"))

        if (parser.current.type == `is`) {
            val isLike = parser.consume().value == "islike"
            val id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "is")).value
            return IsExpression(value, id, isLike)
        }

        return value
    }
}
