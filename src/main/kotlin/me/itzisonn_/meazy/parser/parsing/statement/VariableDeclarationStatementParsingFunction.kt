package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.parsing.PairParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseDataType
import me.itzisonn_.meazy.util.text.translatable

object VariableDeclarationStatementParsingFunction : PairParsingFunction<VariableDeclarationStatement, Set<Modifier>, Boolean>() {
    override fun Parser.parse(first: Set<Modifier>, second: Boolean): VariableDeclarationStatement {
        val modifiers = first
        val canBeConstantWithoutValue = second

        val isConstant = consume(variable, translatable("meazy:parser.expected.keyword", "variable")).value == "val"
        val variableId = consume(id, translatable("meazy:parser.expected", "id")).value
        val dataType = parseDataType()

        if (!isNext(assign)) {
            if (dataType == null) throw InvalidSyntaxException(
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
                translatable("meazy:parser.exception.constant_without_value")
            )
            return VariableDeclarationStatement(modifiers, false, variableId, dataType, NullLiteral())
        }

        consume(assign, translatable("meazy:parser.expected.after", "assign", "id"))

        return VariableDeclarationStatement(
            modifiers,
            isConstant,
            variableId,
            dataType,
            parse(ExpressionParsingFunction)
        )
    }
}
