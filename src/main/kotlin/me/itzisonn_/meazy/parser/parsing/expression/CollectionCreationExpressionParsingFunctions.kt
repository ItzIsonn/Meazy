package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftBracket
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightBracket
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.ListCreationExpression
import me.itzisonn_.meazy.parser.ast.expression.collection_creation.MapCreationExpression
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ListCreationExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        if (!isNext(leftBracket)) return parse(MapCreationExpressionParsingFunction)
        consume(leftBracket, null)

        val list = mutableListOf<Expression>()
        while (!isNext(rightBracket)) {
            list.add(parse(ExpressionParsingFunction))

            if (!isNext(rightBracket)) {
                consume(comma, translatable("parser.expected.separator_expression", "comma", "list_creation"))
            }
        }

        consume(rightBracket, translatable("parser.expected.end_expression", "right_bracket", "list_creation"))
        return ListCreationExpression(list)
    }
}

object MapCreationExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        if (!isNext(leftBrace)) return parse(NullCheckExpressionParsingFunction)
        consume(leftBrace, null)

        val map = linkedMapOf<Expression, Expression>()
        while (!isNext(rightBrace)) {
            val key = parse(ExpressionParsingFunction)
            consume(assign, translatable("parser.expected.separator_expression", "assign", "map_creation"))

            val value = parse(ExpressionParsingFunction)
            map[key] = value

            if (!isNext(rightBrace)) {
                consume(comma, translatable("parser.expected.separator_expression", "comma", "map_creation"))
            }
        }

        consume(rightBrace, translatable("parser.expected.end_expression", "right_brace", "map_creation"))
        return MapCreationExpression(map)
    }
}