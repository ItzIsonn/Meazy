package me.itzisonn_.meazy.parser.pasing_function

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.question
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.lexer.TokenTypes.string
import me.itzisonn_.meazy.parser.*
import me.itzisonn_.meazy.parser.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.parser.DataType.Companion.ofNullable
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers.parse
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.statement.LocalStatementParsingFunction
import me.itzisonn_.meazy.text.translatable
import java.lang.constant.ClassDesc

object ParsingHelper {
    fun parseModifiers(context: ParsingContext): Set<Modifier> {
        val parser = context.parser
        val modifiers = mutableSetOf<Modifier>()

        while (parser.current.type == id) {
            val id = parser.current.value
            val modifier = parse(id)

            if (modifier == null) {
                if (modifiers.isEmpty()) return modifiers
                throw InvalidStatementException(
                    parser.current.line,
                    translatable("meazy:parser.modifier.doesnt_exist", id)
                )
            }

            parser.next()
            modifiers.add(modifier)
        }

        return modifiers
    }

    fun getModifiersFromExtra(extra: Array<out Any?>): Set<Modifier> {
        require(extra.isNotEmpty()) { "Expected Set of Modifiers as extra argument" }
        require(extra[0] is Set<*>) { "Expected Set of Modifiers as extra argument" }
        val set = extra[0] as Set<*>
        
        val result = mutableSetOf<Modifier>()
        for (o in set) {
            if (o is Modifier) result.add(o)
            else throw IllegalArgumentException("Expected Set of Modifiers as extra argument")
        }

        return result
    }

    private fun parseParameter(context: ParsingContext): Parameter {
        val parser = context.parser

        if (parser.current.type != variable) {
            throw UnexpectedTokenException(
                parser.current.line,
                translatable("meazy:parser.expected.start_expression", "variable", "parameter")
            )
        }

        val isConstant = parser.consume().value == "val"
        val id = parser.consume(id, translatable("meazy:parser.expected.after_keyword", "id", "variable")).value

        val lineNumber = parser.current.line
        val dataType = parseDataType(context) ?: throw InvalidSyntaxException(
            lineNumber,
            translatable("meazy:parser.exception.parameter_without_datatype")
        )

        return Parameter(id, dataType, isConstant)
    }

    fun parseParameters(context: ParsingContext): List<Parameter> {
        val parser = context.parser

        parser.consume(
            leftParenthesis,
            translatable("meazy:parser.expected.start_expression", "left_parenthesis", "parameters")
        )
        val parameters = mutableListOf<Parameter>()

        if (parser.current.type != rightParenthesis) {
            parameters.add(parseParameter(context))

            while (parser.current.type == comma) {
                parser.next()
                parameters.add(parseParameter(context))
            }
        }

        parser.consume(
            rightParenthesis,
            translatable("meazy:parser.expected.end_expression", "right_parenthesis", "parameters")
        )
        return parameters
    }

    fun parseArgs(context: ParsingContext): List<Expression> {
        val parser = context.parser
        parser.consume(
            leftParenthesis,
            translatable("meazy:parser.expected.start_expression", "left_parenthesis", "args")
        )
        val args = mutableListOf<Expression>()

        if (parser.current.type != rightParenthesis) {
            args.add(parser.parse(ExpressionParsingFunction))

            while (parser.current.type == comma) {
                parser.next()
                args.add(parser.parse(ExpressionParsingFunction))
            }
        }

        parser.consume(
            rightParenthesis,
            translatable("meazy:parser.expected.end_expression", "right_parenthesis", "args")
        )
        return args
    }

    fun parseDataType(context: ParsingContext): DataType? {
        val parser = context.parser

        if (parser.current.type == colon) {
            parser.consume()
            val dataTypeId = parser.consume(id, translatable("meazy:parser.expected.after", "id", "colon")).value

            if (parser.current.type == question) {
                parser.consume()
                return ofNullable(ClassDesc.of(dataTypeId))
            }

            return ofNonNull(ClassDesc.of(dataTypeId))
        }

        return null
    }

    fun parseBody(context: ParsingContext): List<LocalStatement> {
        val parser = context.parser

        val body = mutableListOf<LocalStatement>()
        parser.consume(newLine, translatable("meazy:parser.expected", "new_line"))
        parser.skipNewLines()

        while (parser.current.type != endOfFile && parser.current.type != rightBrace) {
            body.add(parser.parse(LocalStatementParsingFunction))
            parser.consume(newLine, translatable("meazy:parser.expected", "new_line"))
            parser.skipNewLines()
        }

        return body
    }

    fun parseString(context: ParsingContext): String {
        val parser = context.parser
        val token = parser.current

        val value = parser.consume(string, translatable("meazy:parser.expected", "string")).value
        if (!value.endsWith("\"")) throw InvalidStatementException(
            token.line,
            translatable("meazy:parser.exception.string_quote_not_closed", value.substring(1))
        )
        return value.substring(1, value.length - 1)
    }
}
