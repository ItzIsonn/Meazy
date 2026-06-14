package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.MapCreationExpression
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

object MapCreationExpressionParsingFunction : AbstractParsingFunction<Expression>("map_creation_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser

        if (parser.current.type == leftBrace) {
            parser.consume()
            val map = mutableMapOf<Expression, Expression>()

            while (parser.current.type != rightBrace) {
                val key = parser.parse(ListCreationExpressionParsingFunction)
                parser.consume(assign, translatable("meazy:parser.expected.separator_expression", "assign", "map_creation"))

                val value = parser.parse(ExpressionParsingFunction)
                map[key] = value

                if (parser.current.type != rightBrace) {
                    parser.consume(
                        comma,
                        translatable("meazy:parser.expected.separator_expression", "comma", "map_creation")
                    )
                }
            }

            parser.consume(
                rightBrace,
                translatable("meazy:parser.expected.end_expression", "right_brace", "map_creation")
            )
            return MapCreationExpression(map)
        }

        return parser.parseAfter<Expression>(getDefaultIdentifier("map_creation_expression"))
    }
}
