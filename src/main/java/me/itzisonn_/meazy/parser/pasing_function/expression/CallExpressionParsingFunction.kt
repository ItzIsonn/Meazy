package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.parser.InvalidSyntaxException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

object CallExpressionParsingFunction : AbstractParsingFunction<Expression>("call_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        val expression = parser.parse(PrimaryExpressionParsingFunction)

        if (parser.current.type == leftParenthesis) {
            if (expression !is Identifier) {
                throw InvalidSyntaxException(
                    parser.current.line,
                    translatable("meazy:parser.exception.call_not_identifier")
                )
            }

            val args = ParsingHelper.parseArgs(context)
            return CallExpression(expression, args)
        }

        return expression
    }
}
