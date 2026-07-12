package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement
import me.itzisonn_.meazy.parser.modifier.Modifiers.abstract
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.getModifiersFromExtra
import me.itzisonn_.meazy.parser.pasing_function.parseBody
import me.itzisonn_.meazy.parser.pasing_function.parseDataType
import me.itzisonn_.meazy.parser.pasing_function.parseParameters
import me.itzisonn_.meazy.util.text.translatable

object FunctionDeclarationStatementParsingFunction : ParsingFunction<FunctionDeclarationStatement> {
    override fun Parser.parse(vararg extra: Any?): FunctionDeclarationStatement {
        val modifiers = getModifiersFromExtra(extra).toMutableSet()

        require(extra.size != 1) { "Expected boolean as extra argument" }
        require(extra[1] is Boolean) { "Expected boolean as extra argument" }
        val canBeAbstractWithoutModifier = extra[1] as Boolean

        next(function, translatable("meazy:parser.expected.keyword", "function"))

        var classId: String? = null
        var functionId = consume(id, translatable("meazy:parser.expected.after_keyword", "id", "function")).value
        if (current.type == dot) {
            next()
            classId = functionId
            functionId = consume(id, translatable("meazy:parser.expected", "id")).value
        }

        val parameters = parseParameters()
        val dataType = parseDataType()

        if (abstract in modifiers || (canBeAbstractWithoutModifier && current.type == newLine)) {
            modifiers.add(abstract)
            return FunctionDeclarationStatement(modifiers, functionId, parameters, mutableListOf(), dataType)
        }

        val body: List<LocalStatement>
        val returnDataTypeValue: Expression?

        if (current.type == assign) {
            next()
            val expression = parse(ExpressionParsingFunction)
            body = listOf(ReturnStatement(expression))
            returnDataTypeValue = expression
        }
        else {
            next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "function_body"))
            body = parseBody()
            next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "function_body"))
            returnDataTypeValue = null
        }

        return FunctionDeclarationStatement(modifiers, functionId, classId, parameters, body, dataType, returnDataTypeValue)
    }
}
