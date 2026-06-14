package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidSyntaxException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.literal.NullLiteral
import me.itzisonn_.meazy.parser.ast.statement.VariableDeclarationStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class VariableDeclarationStatementParsingFunction :
    AbstractParsingFunction<VariableDeclarationStatement>("variable_declaration_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): VariableDeclarationStatement {
        val parser = context.parser
        val modifiers = ParsingHelper.getModifiersFromExtra(extra)

        require(extra.size != 1) { "Expected boolean as extra argument" }
        require(extra[1] is Boolean) { "Expected boolean as extra argument" }
        val canBeConstantWithoutValue = extra[1] as Boolean

        val isConstant = parser.consume(variable, translatable("meazy:parser.expected.keyword", "variable")).value == "val"
        val id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value
        val dataType = ParsingHelper.parseDataType(context)

        if (parser.current.type != assign) {
            if (dataType == null) throw InvalidSyntaxException(
                parser.current.line,
                translatable("meazy:parser.exception.variable_without_datatype_and_value")
            )

            if (canBeConstantWithoutValue) return VariableDeclarationStatement(
                modifiers,
                isConstant,
                id,
                dataType,
                null
            )
            if (isConstant) throw InvalidSyntaxException(
                parser.current.line,
                translatable("meazy:parser.exception.constant_without_value")
            )
            return VariableDeclarationStatement(modifiers, false, id, dataType, NullLiteral())
        }

        parser.next(assign, translatable("meazy:parser.expected.after", "assign", "id"))

        return VariableDeclarationStatement(
            modifiers,
            isConstant,
            id,
            dataType,
            parser.parse<Expression>(getDefaultIdentifier("expression"))
        )
    }
}
