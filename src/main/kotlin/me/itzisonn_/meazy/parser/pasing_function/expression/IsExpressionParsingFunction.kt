package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`is`
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.IsExpression
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable

object IsExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        val value = parse(NegationExpressionParsingFunction)

        if (current.type == `is`) {
            val isLike = consume().value == "islike"
            val id = consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "is")).value
            return IsExpression(value, id, isLike)
        }

        return value
    }
}
