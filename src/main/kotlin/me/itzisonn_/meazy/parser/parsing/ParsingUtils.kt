package me.itzisonn_.meazy.parser.parsing

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.comma
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
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.parsing.statement.LocalStatementParsingFunction
import me.itzisonn_.meazy.util.text.translatable
import java.lang.constant.ClassDesc

fun Parser.parseModifiers(): Set<Modifier> {
    val modifiers = mutableSetOf<Modifier>()

    while (isNext(id)) {
        val id = find(id, null).value
        val modifier = Modifiers.get(id)

        if (modifier == null) {
            if (modifiers.isEmpty()) return modifiers
            throw InvalidStatementException(
                translatable("meazy:parser.modifier.doesnt_exist", id)
            )
        }

        consume()
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
    val isConstant = consume(variable, translatable("meazy:parser.expected.start_expression", "variable", "parameter")).value == "val"
    val id = consume(id, translatable("meazy:parser.expected.after_keyword", "id", "variable")).value

    val dataType = parseDataType() ?: throw InvalidSyntaxException(
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

    if (!isNext(rightParenthesis)) {
        parameters.add(parseParameter())

        while (isNext(comma)) {
            consume(comma, null)
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

    if (!isNext(rightParenthesis)) {
        args.add(parse(ExpressionParsingFunction))

        while (isNext(comma)) {
            consume(comma, null)
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
    if (!isNext(colon)) return null
    consume(colon, null)

    val dataTypeId = consume(id, translatable("meazy:parser.expected.after", "id", "colon")).value

    if (isNext(question)) {
        consume(question, null)
        return ofNullable(ClassDesc.of(dataTypeId))
    }

    return ofNonNull(ClassDesc.of(dataTypeId))
}

fun Parser.parseBody(): List<LocalStatement> {
    val body = mutableListOf<LocalStatement>()
    consume(newLine, translatable("meazy:parser.expected", "new_line"))

    while (!isEndOfFile() && !isNext(rightBrace)) {
        body.add(parse(LocalStatementParsingFunction))
        consume(newLine, translatable("meazy:parser.expected", "new_line"))
    }

    return body
}

fun Parser.parseString(): String {
    val stringToken = consume(string, translatable("meazy:parser.expected", "string"))
    val value = stringToken.value

    if (!value.endsWith("\"")) throw InvalidStatementException(
        stringToken.line,
        translatable("meazy:parser.exception.string_quote_not_closed", value.substring(1))
    )

    return value.substring(1, value.length - 1)
}