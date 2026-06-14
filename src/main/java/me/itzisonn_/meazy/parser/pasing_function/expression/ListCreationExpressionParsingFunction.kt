package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.leftBracket
import me.itzisonn_.meazy.lexer.TokenTypes.rightBracket
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.ListCreationExpression
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

object ListCreationExpressionParsingFunction : AbstractParsingFunction<Expression>("list_creation_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser

        if (parser.current.type == leftBracket) {
            parser.next()
            val list = mutableListOf<Expression>()

            while (parser.current.type != rightBracket) {
                list.add(parser.parse(ExpressionParsingFunction))

                if (parser.current.type != rightBracket) {
                    parser.consume(
                        comma,
                        translatable("meazy:parser.expected.separator_expression", "comma", "list_creation")
                    )
                }
            }

            parser.consume(
                rightBracket,
                translatable("meazy:parser.expected.end_expression", "right_bracket", "list_creation")
            )

            return ListCreationExpression(list)
        }

        return parser.parse(MapCreationExpressionParsingFunction)
    }
}
