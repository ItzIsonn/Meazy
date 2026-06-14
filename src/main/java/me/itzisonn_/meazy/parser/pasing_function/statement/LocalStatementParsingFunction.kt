package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorAssign
import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorPostfix
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.lexer.TokenTypes.base
import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.lexer.TokenTypes.`continue`
import me.itzisonn_.meazy.lexer.TokenTypes.`for`
import me.itzisonn_.meazy.lexer.TokenTypes.`if`
import me.itzisonn_.meazy.lexer.TokenTypes.`return`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.lexer.TokenTypes.`while`
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.InvalidSyntaxException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.text.translatable

object LocalStatementParsingFunction : AbstractParsingFunction<LocalStatement>("local_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): LocalStatement {
        val parser = context.parser
        val modifiers = ParsingHelper.parseModifiers(context)

        if (parser.current.type == variable) {
            return parser.parse(
                VariableDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }
        if (!modifiers.isEmpty()) throw InvalidSyntaxException(
            parser.current.line,
            translatable("meazy:parser.modifier.unexpected")
        )

        if (parser.current.type == `if`) return parser.parse(IfStatementParsingFunction)
        if (parser.current.type == `for`) return parser.parse(ForeachStatementParsingFunction)
        if (parser.current.type == `while`) return parser.parse(WhileStatementParsingFunction)
        if (parser.current.type == `return`) return parser.parse(ReturnStatementParsingFunction)
        if (parser.current.type == `continue`) return parser.parse(ContinueStatementParsingFunction)
        if (parser.current.type == `break`) return parser.parse(BreakStatementParsingFunction)
        if (parser.current.type == base) return parser.parse(BaseCallStatementParsingFunction)

        if (parser.currentLineHasToken(assign) || parser.currentLineHasToken(operatorAssign)) {
            return parser.parse(AssignmentStatementParsingFunction)
        }

        if (parser.currentLineHasToken(operatorPostfix)) {
            return parser.parse(PostfixStatementParsingFunction)
        }

        return when (val expression = parser.parse(ExpressionParsingFunction)) {
            is CallExpression -> expression
            is MemberExpression -> expression
            is OperatorExpression -> expression
            else -> throw InvalidStatementException(
                parser.current.line,
                translatable("meazy:parser.exception.statement")
            )
        }
    }
}
