package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseArgs
import me.itzisonn_.meazy.util.text.translatable

object CallExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        val expression = parse(PrimaryExpressionParsingFunction)

        if (isNext(leftParenthesis)) {
            if (expression !is Identifier) {
                throw InvalidSyntaxException(
                    translatable("parser.exception.call_not_identifier")
                )
            }

            val args = parseArgs()
            return CallExpression(expression.id, args)
        }

        return expression
    }
}
