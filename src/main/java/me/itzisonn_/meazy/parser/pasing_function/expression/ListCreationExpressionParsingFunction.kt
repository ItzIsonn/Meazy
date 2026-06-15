package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.leftBracket
import me.itzisonn_.meazy.lexer.TokenTypes.rightBracket
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.ListCreationExpression
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable

object ListCreationExpressionParsingFunction : ParsingFunction<Expression>("list_creation_expression") {
    override fun Parser.parse(vararg extra: Any?): Expression {
        if (current.type == leftBracket) {
            next()
            val list = mutableListOf<Expression>()

            while (current.type != rightBracket) {
                list.add(parse(ExpressionParsingFunction))

                if (current.type != rightBracket) {
                    consume(
                        comma,
                        translatable("meazy:parser.expected.separator_expression", "comma", "list_creation")
                    )
                }
            }

            consume(
                rightBracket,
                translatable("meazy:parser.expected.end_expression", "right_bracket", "list_creation")
            )

            return ListCreationExpression(list)
        }

        return parse(MapCreationExpressionParsingFunction)
    }
}
