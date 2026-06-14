package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.boolean
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`null`
import me.itzisonn_.meazy.lexer.TokenTypes.number
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.string
import me.itzisonn_.meazy.lexer.TokenTypes.`this`
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.identifier.ClassIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.FunctionIdentifier
import me.itzisonn_.meazy.parser.ast.expression.identifier.VariableIdentifier
import me.itzisonn_.meazy.parser.ast.expression.literal.*
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

object PrimaryExpressionParsingFunction : AbstractParsingFunction<Expression>("primary_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        val token = parser.current
        val tokenType = token.type

        if (tokenType == TokenTypes.id) {
            if (parser.size > parser.pos + 1 && parser[parser.pos + 1].type == leftParenthesis) {
                val id = parser.consume().value
                return if (Character.isUpperCase(id[0])) ClassIdentifier(id)
                else FunctionIdentifier(id)
            }

            if (parser.pos > 0 && parser[parser.pos - 1].type == dot) {
                return VariableIdentifier(parser.consume().value)
            }

            val id = parser.consume().value
            return if (Character.isUpperCase(id[0])) ClassIdentifier(id) else VariableIdentifier(id)
        }
        if (tokenType == `null`) {
            parser.consume()
            return NullLiteral()
        }
        if (tokenType == number) return NumberLiteral(parser.consume().value)
        if (tokenType == string) return StringLiteral(ParsingHelper.parseString(context))
        if (tokenType == boolean) return BooleanLiteral(parser.consume().value.toBoolean())
        if (tokenType == `this`) {
            parser.consume()
            return ThisLiteral()
        }
        if (tokenType == leftParenthesis) {
            parser.consume()
            val value = parser.parse(ExpressionParsingFunction)
            parser.consume(rightParenthesis, translatable("meazy:parser.expected", "right_parenthesis"))
            return value
        }

        throw InvalidStatementException(token.line, translatable("meazy:parser.exception.cant_parse", tokenType.id))
    }
}
