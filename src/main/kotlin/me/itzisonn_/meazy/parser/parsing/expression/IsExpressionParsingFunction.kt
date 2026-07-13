package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`is`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.IsExpression
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object IsExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        val value = parse(NegationExpressionParsingFunction)

        if (isNext(`is`)) {
            val isLike = consume(`is`, null).value == "islike"
            val id = consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "is")).value
            return IsExpression(value, id, isLike)
        }

        return value
    }
}
