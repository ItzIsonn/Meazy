package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
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
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class LocalStatementParsingFunction : AbstractParsingFunction<LocalStatement>("local_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): LocalStatement {
        val parser = context.parser
        val modifiers = ParsingHelper.parseModifiers(context)

        if (parser.current.type == variable) {
            return parser.parse<VariableDeclarationStatement>(
                getDefaultIdentifier("variable_declaration_statement"),
                modifiers,
                false
            )
        }
        if (!modifiers.isEmpty()) throw InvalidSyntaxException(
            parser.current.line,
            translatable("meazy:parser.modifier.unexpected")
        )

        if (parser.current.type == `if`) return parser.parse<IfStatement>(
            getDefaultIdentifier("if_statement")
        )
        if (parser.current.type == `for`) return parser.parse<ForeachStatement>(
            getDefaultIdentifier("foreach_statement")
        )
        if (parser.current.type == `while`) return parser.parse<WhileStatement>(
            getDefaultIdentifier("while_statement")
        )
        if (parser.current.type == `return`) return parser.parse<ReturnStatement>(
            getDefaultIdentifier("return_statement")
        )
        if (parser.current.type == `continue`) return parser.parse<ContinueStatement>(
            getDefaultIdentifier("continue_statement")
        )
        if (parser.current.type == `break`) return parser.parse<BreakStatement>(
            getDefaultIdentifier("break_statement")
        )
        if (parser.current.type == base) return parser.parse<BaseCallStatement>(
            getDefaultIdentifier("base_call_statement")
        )

        if (parser.currentLineHasToken(assign) || parser.currentLineHasToken(operatorAssign)) {
            return parser.parse<AssignmentStatement>(
                getDefaultIdentifier("assignment_statement")
            )
        }

        if (parser.currentLineHasToken(operatorPostfix)) {
            return parser.parse<AssignmentStatement>(
                getDefaultIdentifier("postfix_statement")
            )
        }

        return when (val expression = parser.parse<Expression>(getDefaultIdentifier("expression"))) {
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
