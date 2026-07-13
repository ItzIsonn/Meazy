package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseArgs
import me.itzisonn_.meazy.util.text.translatable

object CallExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        val expression = parse(PrimaryExpressionParsingFunction)

        if (isNext(leftParenthesis)) {
            if (expression !is Identifier) {
                throw InvalidSyntaxException(
                    translatable("meazy:parser.exception.call_not_identifier")
                )
            }

            val args = parseArgs()
            return CallExpression(expression, args)
        }

        return expression
    }
}
