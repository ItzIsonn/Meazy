package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorAssign
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class AssignmentStatementParsingFunction : AbstractParsingFunction<AssignmentStatement>("assignment_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): AssignmentStatement {
        val parser = context.parser
        val left = parser.parse<Expression>(getDefaultIdentifier("expression"))

        if (parser.current.type == assign) {
            parser.next()
            val value = parser.parse<Expression>(getDefaultIdentifier("expression"))
            return AssignmentStatement(left, value)
        }
        else if (parser.current.type in operatorAssign) {
            val token = parser.consume()

            val value: Expression = OperatorExpression(
                left,
                parser.parse<Expression>(getDefaultIdentifier("expression")),
                token.value.replace("=$".toRegex(), ""), OperatorType.INFIX
            )

            return AssignmentStatement(left, value)
        }

        throw UnexpectedTokenException(parser.current.line, translatable("meazy:parser.expected.separator_statement", "assign", "assignment"))
    }
}
