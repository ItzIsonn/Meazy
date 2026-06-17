package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.boolean
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`null`
import me.itzisonn_.meazy.lexer.TokenTypes.number
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.string
import me.itzisonn_.meazy.lexer.TokenTypes.`this`
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.expression.literal.*
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseString
import me.itzisonn_.meazy.text.translatable

object PrimaryExpressionParsingFunction : ParsingFunction<Expression> {
    override fun Parser.parse(vararg extra: Any?): Expression {
        val token = current
        val tokenType = token.type

        if (tokenType == id) {
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
        if (tokenType == `null`) {
            consume()
            return NullLiteral()
        }
        if (tokenType == number) return NumberLiteral(consume().value)
        if (tokenType == string) return StringLiteral(parseString())
        if (tokenType == boolean) return BooleanLiteral(consume().value.toBoolean())
        if (tokenType == `this`) {
            consume()
            return ThisLiteral()
        }
        if (tokenType == leftParenthesis) {
            consume()
            val value = parse(ExpressionParsingFunction)
            consume(rightParenthesis, translatable("meazy:parser.expected", "right_parenthesis"))
            return value
        }

        throw InvalidStatementException(token.line, translatable("meazy:parser.exception.cant_parse", tokenType.id))
    }
}
