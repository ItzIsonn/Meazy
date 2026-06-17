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

fun Parser.parseModifiers(): Set<Modifier> {
    val modifiers = mutableSetOf<Modifier>()

    while (current.type == id) {
        val id = current.value
        val modifier = parse(id)

        if (modifier == null) {
            if (modifiers.isEmpty()) return modifiers
            throw InvalidStatementException(
                current.line,
                translatable("meazy:parser.modifier.doesnt_exist", id)
            )
        }

        next()
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

private fun Parser.parseParameter(): Parameter {
    if (current.type != variable) {
        throw UnexpectedTokenException(
            current.line,
            translatable("meazy:parser.expected.start_expression", "variable", "parameter")
        )
    }

    val isConstant = consume().value == "val"
    val id = consume(id, translatable("meazy:parser.expected.after_keyword", "id", "variable")).value

    val lineNumber = current.line
    val dataType = parseDataType() ?: throw InvalidSyntaxException(
        lineNumber,
        translatable("meazy:parser.exception.parameter_without_datatype")
    )

    return Parameter(id, dataType, isConstant)
}

fun Parser.parseParameters(): List<Parameter> {
    consume(
        leftParenthesis,
        translatable("meazy:parser.expected.start_expression", "left_parenthesis", "parameters")
    )
    val parameters = mutableListOf<Parameter>()

    if (current.type != rightParenthesis) {
        parameters.add(parseParameter())

        while (current.type == comma) {
            next()
            parameters.add(parseParameter())
        }
    }

    consume(
        rightParenthesis,
        translatable("meazy:parser.expected.end_expression", "right_parenthesis", "parameters")
    )
    return parameters
}

fun Parser.parseArgs(): List<Expression> {
    consume(
        leftParenthesis,
        translatable("meazy:parser.expected.start_expression", "left_parenthesis", "args")
    )
    val args = mutableListOf<Expression>()

    if (current.type != rightParenthesis) {
        args.add(parse(ExpressionParsingFunction))

        while (current.type == comma) {
            next()
            args.add(parse(ExpressionParsingFunction))
        }
    }

    consume(
        rightParenthesis,
        translatable("meazy:parser.expected.end_expression", "right_parenthesis", "args")
    )
    return args
}

fun Parser.parseDataType(): DataType? {
    if (current.type == colon) {
        consume()
        val dataTypeId = consume(id, translatable("meazy:parser.expected.after", "id", "colon")).value

        if (current.type == question) {
            consume()
            return ofNullable(ClassDesc.of(dataTypeId))
        }

        return ofNonNull(ClassDesc.of(dataTypeId))
    }

    return null
}

fun Parser.parseBody(): List<LocalStatement> {
    val body = mutableListOf<LocalStatement>()
    consume(newLine, translatable("meazy:parser.expected", "new_line"))
    skipNewLines()

    while (current.type != endOfFile && current.type != rightBrace) {
        body.add(parse(LocalStatementParsingFunction))
        consume(newLine, translatable("meazy:parser.expected", "new_line"))
        skipNewLines()
    }

    return body
}

fun Parser.parseString(): String {
    val token = current
    val value = consume(string, translatable("meazy:parser.expected", "string")).value

    if (!value.endsWith("\"")) throw InvalidStatementException(
        token.line,
        translatable("meazy:parser.exception.string_quote_not_closed", value.substring(1))
    )

    return value.substring(1, value.length - 1)
}