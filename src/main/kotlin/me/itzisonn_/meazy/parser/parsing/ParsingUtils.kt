package me.itzisonn_.meazy.parser.parsing

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.question
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.lexer.TokenTypes.string
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNonNull
import me.itzisonn_.meazy.runtime.data.DataType.Companion.ofNullable
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.runtime.data.modifier.Modifiers
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.runtime.data.DataType
import me.itzisonn_.meazy.runtime.data.Parameter
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
                translatable("parser.modifier.doesnt_exist", id)
            )
        }

        consume(TokenTypes.id, null)
        modifiers.add(modifier)
    }

    return modifiers
}

private fun Parser.parseParameter(): Parameter {
    val isConstant = consume(variable, translatable("parser.expected.start_expression", "variable", "parameter")).value == "val"
    val id = consume(id, translatable("parser.expected.after_keyword", "id", "variable")).value

    val dataType = parseDataType() ?: throw InvalidSyntaxException(
        translatable("parser.exception.parameter_without_datatype")
    )

    return Parameter(id, dataType, isConstant)
}

fun Parser.parseParameters(): List<Parameter> {
    consume(
        leftParenthesis,
        translatable("parser.expected.start_expression", "left_parenthesis", "parameters")
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
        translatable("parser.expected.end_expression", "right_parenthesis", "parameters")
    )
    return parameters
}

fun Parser.parseArgs(): List<Expression> {
    consume(
        leftParenthesis,
        translatable("parser.expected.start_expression", "left_parenthesis", "args")
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
        translatable("parser.expected.end_expression", "right_parenthesis", "args")
    )
    return args
}

fun Parser.parseDataType(): DataType? {
    if (!isNext(colon)) return null
    consume(colon, null)

    val dataTypeId = consume(id, translatable("parser.expected.after", "id", "colon")).value

    if (isNext(question)) {
        consume(question, null)
        return ofNullable(ClassDesc.of(dataTypeId))
    }

    return ofNonNull(ClassDesc.of(dataTypeId))
}

fun <T : Statement> Parser.parseBody(parsingFunction: EmptyParsingFunction<T>): List<T> {
    val body = mutableListOf<T>()
    consume(leftBrace, translatable("parser.expected.start", "left_brace", "body"))

    while (!isEndOfFile() && !isNext(rightBrace)) {
        body.add(parse(parsingFunction))

        if (!isEndOfFile() && !isNext(rightBrace)) {
            consume(newLine, translatable("parser.expected", "new_line"))
        }
    }

    consume(rightBrace, translatable("parser.expected.end", "right_brace", "body"))
    return body
}

fun Parser.parseString(): String {
    val stringToken = consume(string, translatable("parser.expected", "string"))
    val value = stringToken.value

    if (!value.endsWith("\"")) throw InvalidStatementException(
        stringToken.line,
        translatable("parser.exception.string_quote_not_closed", value.substring(1))
    )

    return value.substring(1, value.length - 1)
}