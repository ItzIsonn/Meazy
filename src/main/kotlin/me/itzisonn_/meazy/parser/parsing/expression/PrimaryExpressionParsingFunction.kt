package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.boolean
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`null`
import me.itzisonn_.meazy.lexer.TokenTypes.number
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.string
import me.itzisonn_.meazy.lexer.TokenTypes.`this`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.expression.literal.*
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseString
import me.itzisonn_.meazy.util.text.translatable

object PrimaryExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        if (isNext(id)) {
            find(id, null)

            if (size > pos + 1 && this[pos + 1].type == leftParenthesis) {
                val id = consume().value
                return if (Character.isUpperCase(id[0])) ClassIdentifier(id)
                else FunctionIdentifier(id)
            }

            if (pos > 0 && this[pos - 1].type == dot) {
                return VariableIdentifier(consume().value)
            }

            val id = consume().value
            return if (Character.isUpperCase(id[0])) ClassIdentifier(id) else VariableIdentifier(id)
        }

        if (isNext(`null`)) {
            consume(`null`, null)
            return NullLiteral()
        }

        if (isNext(number)) return NumberLiteral(consume(number, null).value)
        if (isNext(string)) return StringLiteral(parseString())
        if (isNext(boolean)) return BooleanLiteral(consume(boolean, null).value.toBoolean())

        if (isNext(`this`)) {
            consume(`this`, null)
            return ThisLiteral()
        }

        if (isNext(leftParenthesis)) {
            consume(leftParenthesis, null)
            val value = parse(ExpressionParsingFunction)
            consume(rightParenthesis, translatable("meazy:parser.expected", "right_parenthesis"))
            return value
        }

        throw InvalidStatementException(translatable("meazy:parser.exception.cant_parse", this[pos].type.id))
    }
}
