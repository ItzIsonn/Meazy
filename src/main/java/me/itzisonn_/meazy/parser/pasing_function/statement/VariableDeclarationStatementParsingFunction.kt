package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidSyntaxException
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.getModifiersFromExtra
import me.itzisonn_.meazy.parser.pasing_function.parseDataType
import me.itzisonn_.meazy.text.translatable

object VariableDeclarationStatementParsingFunction : ParsingFunction<VariableDeclarationStatement> {
    override fun Parser.parse(vararg extra: Any?): VariableDeclarationStatement {
        val modifiers = getModifiersFromExtra(extra)

        require(extra.size != 1) { "Expected boolean as extra argument" }
        require(extra[1] is Boolean) { "Expected boolean as extra argument" }
        val canBeConstantWithoutValue = extra[1] as Boolean

        val isConstant = consume(variable, translatable("meazy:parser.expected.keyword", "variable")).value == "val"
        val variableId = consume(id, translatable("meazy:parser.expected", "id")).value
        val dataType = parseDataType()

        if (current.type != assign) {
            if (dataType == null) throw InvalidSyntaxException(
                current.line,
                translatable("meazy:parser.exception.variable_without_datatype_and_value")
            )

            if (canBeConstantWithoutValue) return VariableDeclarationStatement(
                modifiers,
                isConstant,
                variableId,
                dataType,
                null
            )
            if (isConstant) throw InvalidSyntaxException(
                current.line,
                translatable("meazy:parser.exception.constant_without_value")
            )
            return VariableDeclarationStatement(modifiers, false, variableId, dataType, NullLiteral())
        }

        next(assign, translatable("meazy:parser.expected.after", "assign", "id"))

        return VariableDeclarationStatement(
            modifiers,
            isConstant,
            variableId,
            dataType,
            parse(ExpressionParsingFunction)
        )
    }
}
