package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.boolean
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`null`
import me.itzisonn_.meazy.lexer.TokenTypes.number
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.string
import me.itzisonn_.meazy.lexer.TokenTypes.`this`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.Identifier
import me.itzisonn_.meazy.parser.ast.expression.literal.*
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseString
import me.itzisonn_.meazy.util.text.translatable

object PrimaryExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        if (isNext(id)) return Identifier(consume(id, null).value)
        if (isNext(number)) return NumberLiteral(consume(number, null).value)
        if (isNext(string)) return StringLiteral(parseString())
        if (isNext(boolean)) return BooleanLiteral(consume(boolean, null).value.toBoolean())

        if (isNext(`null`)) {
            consume(`null`, null)
            return NullLiteral()
        }

        if (isNext(`this`)) {
            consume(`this`, null)
            return ThisLiteral()
        }

        if (isNext(leftParenthesis)) {
            consume(leftParenthesis, null)
            val value = parse(ExpressionParsingFunction)
            consume(rightParenthesis, translatable("parser.expected", "right_parenthesis"))
            return value
        }

        throw UnexpectedTokenException(translatable("parser.expected.expression", "primary"))
    }
}
