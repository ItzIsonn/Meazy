package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement
import me.itzisonn_.meazy.parser.modifier.Modifiers.abstract
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.text.translatable

object FunctionDeclarationStatementParsingFunction :
    AbstractParsingFunction<FunctionDeclarationStatement>("function_declaration_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): FunctionDeclarationStatement {
        val parser = context.parser
        val modifiers = ParsingHelper.getModifiersFromExtra(extra).toMutableSet()

        require(extra.size != 1) { "Expected boolean as extra argument" }
        require(extra[1] is Boolean) { "Expected boolean as extra argument" }
        val canBeAbstractWithoutModifier = extra[1] as Boolean

        parser.next(function, translatable("meazy:parser.expected.keyword", "function"))

        var classId: String? = null
        var id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "function")).value
        if (parser.current.type == dot) {
            parser.next()
            classId = id
            id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value
        }

        val parameters = ParsingHelper.parseParameters(context)
        val dataType = ParsingHelper.parseDataType(context)

        if (abstract in modifiers || (canBeAbstractWithoutModifier && parser.current.type == newLine)) {
            modifiers.add(abstract)
            return FunctionDeclarationStatement(modifiers, id, parameters, mutableListOf(), dataType)
        }

        val body: List<LocalStatement>
        val returnDataTypeValue: Expression?

        if (parser.current.type == assign) {
            parser.next()
            val expression = parser.parse(ExpressionParsingFunction)
            body = listOf(ReturnStatement(expression))
            returnDataTypeValue = expression
        }
        else {
            parser.next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "function_body"))
            body = ParsingHelper.parseBody(context)
            parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "function_body"))
            returnDataTypeValue = null
        }

        return FunctionDeclarationStatement(modifiers, id, classId, parameters, body, dataType, returnDataTypeValue)
    }
}
