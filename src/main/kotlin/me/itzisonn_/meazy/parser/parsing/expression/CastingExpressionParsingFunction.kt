package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`as`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.CastingExpression
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object CastingExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        val value = parse(NegationExpressionParsingFunction)

        if (isNext(`as`)) {
            val isSafe = consume(`as`, null).value.endsWith("?")
            val id = consume(TokenTypes.id, translatable("parser.expected.after_keyword", "id", "as")).value
            return CastingExpression(value, id, isSafe)
        }

        return value
    }
}
