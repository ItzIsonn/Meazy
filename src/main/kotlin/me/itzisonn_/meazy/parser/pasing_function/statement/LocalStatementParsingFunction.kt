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
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseModifiers
import me.itzisonn_.meazy.text.translatable

object LocalStatementParsingFunction : ParsingFunction<LocalStatement> {
    override fun Parser.parse(vararg extra: Any?): LocalStatement {
        val modifiers = parseModifiers()

        if (current.type == variable) {
            return parse(
                VariableDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }
        if (!modifiers.isEmpty()) throw InvalidSyntaxException(
            current.line,
            translatable("meazy:parser.modifier.unexpected")
        )

        if (current.type == `if`) return parse(IfStatementParsingFunction)
        if (current.type == `for`) return parse(ForeachStatementParsingFunction)
        if (current.type == `while`) return parse(WhileStatementParsingFunction)
        if (current.type == `return`) return parse(ReturnStatementParsingFunction)
        if (current.type == `continue`) return parse(ContinueStatementParsingFunction)
        if (current.type == `break`) return parse(BreakStatementParsingFunction)
        if (current.type == base) return parse(BaseCallStatementParsingFunction)

        if (currentLineHasToken(assign) || currentLineHasToken(operatorAssign)) {
            return parse(AssignmentStatementParsingFunction)
        }

        if (currentLineHasToken(operatorPostfix)) {
            return parse(PostfixStatementParsingFunction)
        }

        return when (val expression = parse(ExpressionParsingFunction)) {
            is CallExpression -> expression
            is MemberExpression -> expression
            is OperatorExpression -> expression
            else -> throw InvalidStatementException(
                current.line,
                translatable("meazy:parser.exception.statement")
            )
        }
    }
}
