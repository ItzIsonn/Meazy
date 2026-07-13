package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.FunctionDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.ast.statement.ReturnStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers.abstract
import me.itzisonn_.meazy.parser.parsing.PairParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.parser.parsing.parseDataType
import me.itzisonn_.meazy.parser.parsing.parseParameters
import me.itzisonn_.meazy.util.text.translatable

object FunctionDeclarationStatementParsingFunction : PairParsingFunction<FunctionDeclarationStatement, Set<Modifier>, Boolean>() {
    override fun Parser.parse(first: Set<Modifier>, second: Boolean): FunctionDeclarationStatement {
        val modifiers = first.toMutableSet()
        val canBeAbstractWithoutModifier = second

        consume(function, translatable("meazy:parser.expected.keyword", "function"))

        var classId: String? = null
        var functionId = consume(id, translatable("meazy:parser.expected.after_keyword", "id", "function")).value
        if (isNext(dot)) {
            consume(dot, null)
            classId = functionId
            functionId = consume(id, translatable("meazy:parser.expected", "id")).value
        }

        val parameters = parseParameters()
        val dataType = parseDataType()

        if (abstract in modifiers || (canBeAbstractWithoutModifier && isNext(newLine))) {
            modifiers.add(abstract)
            return FunctionDeclarationStatement(modifiers, functionId, parameters, mutableListOf(), dataType)
        }

        val body: List<LocalStatement>
        val returnDataTypeValue: Expression?

        if (isNext(assign)) {
            consume(assign, null)
            val expression = parse(ExpressionParsingFunction)
            body = listOf(ReturnStatement(expression))
            returnDataTypeValue = expression
        }
        else {
            consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "function_body"))
            body = parseBody()
            consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "function_body"))
            returnDataTypeValue = null
        }

        return FunctionDeclarationStatement(modifiers, functionId, classId, parameters, body, dataType, returnDataTypeValue)
    }
}
