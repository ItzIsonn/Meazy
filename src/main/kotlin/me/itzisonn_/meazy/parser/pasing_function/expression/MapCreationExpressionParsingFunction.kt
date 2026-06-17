package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.MapCreationExpression
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable

object MapCreationExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        if (current.type == leftBrace) {
            consume()
            val map = mutableMapOf<Expression, Expression>()

            while (current.type != rightBrace) {
                val key = parse(ListCreationExpressionParsingFunction)
                consume(assign, translatable("meazy:parser.expected.separator_expression", "assign", "map_creation"))

                val value = parse(ExpressionParsingFunction)
                map[key] = value

                if (current.type != rightBrace) {
                    consume(
                        comma,
                        translatable("meazy:parser.expected.separator_expression", "comma", "map_creation")
                    )
                }
            }

            consume(
                rightBrace,
                translatable("meazy:parser.expected.end_expression", "right_brace", "map_creation")
            )
            return MapCreationExpression(map)
        }

        return parse(NullCheckExpressionParsingFunction)
    }
}
