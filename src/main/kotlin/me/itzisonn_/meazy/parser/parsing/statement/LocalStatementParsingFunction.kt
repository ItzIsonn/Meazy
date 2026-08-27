package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.base
import me.itzisonn_.meazy.lexer.TokenTypes.`break`
import me.itzisonn_.meazy.lexer.TokenTypes.`continue`
import me.itzisonn_.meazy.lexer.TokenTypes.`for`
import me.itzisonn_.meazy.lexer.TokenTypes.`if`
import me.itzisonn_.meazy.lexer.TokenTypes.`return`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.lexer.TokenTypes.`while`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseModifiers
import me.itzisonn_.meazy.util.text.translatable

object LocalStatementParsingFunction : EmptyParsingFunction<LocalStatement>() {
    override fun Parser.parse(): LocalStatement {
        val modifiers = parseModifiers()

        if (isNext(variable)) {
            return parse(
                VariableDeclarationStatementParsingFunction,
                Pair(modifiers, false)
            )
        }
        if (modifiers.isNotEmpty()) throw InvalidSyntaxException(
            translatable("parser.modifier.unexpected")
        )

        if (isNext(`if`)) return parse(IfStatementParsingFunction)
        if (isNext(`for`)) return parse(ForeachStatementParsingFunction)
        if (isNext(`while`)) return parse(WhileStatementParsingFunction)
        if (isNext(`return`)) return parse(ReturnStatementParsingFunction)
        if (isNext(`continue`)) return parse(ContinueStatementParsingFunction)
        if (isNext(`break`)) return parse(BreakStatementParsingFunction)
        if (isNext(base)) return parse(BaseCallStatementParsingFunction)

        tryParse(AssignmentStatementParsingFunction)?.let { return it }
        tryParse(PostfixStatementParsingFunction)?.let { return it }

        return when (val expression = parse(ExpressionParsingFunction)) {
            is CallExpression -> expression
            is MemberExpression -> expression
            is OperatorExpression -> expression
            else -> throw InvalidStatementException(translatable("parser.exception.statement"))
        }
    }
}
