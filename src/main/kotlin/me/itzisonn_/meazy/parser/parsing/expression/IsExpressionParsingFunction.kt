package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`is`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.IsExpression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.runtime.data.operator.Operators
import me.itzisonn_.meazy.util.text.translatable

object IsExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        val value = parse(NegationExpressionParsingFunction)

        if (isNext(`is`)) {
            val isInverted = consume(`is`, null).value.startsWith("!")
            val id = consume(TokenTypes.id, translatable("parser.expected.after_keyword", "id", "is")).value

            val expression = IsExpression(value, id)
            return if (isInverted) OperatorExpression(expression, null, Operators.inversion)
            else expression
        }

        return value
    }
}
