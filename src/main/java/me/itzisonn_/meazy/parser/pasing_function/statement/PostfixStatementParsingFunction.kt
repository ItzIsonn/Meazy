package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorPostfix
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class PostfixStatementParsingFunction : AbstractParsingFunction<Statement>("postfix_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Statement {
        val parser = context.parser

        val left = parser.parse<Expression>(getDefaultIdentifier("expression"))
        val token = parser.consume(
            operatorPostfix,
            translatable("meazy:parser.expected.end_statement", "operator_postfix", "postfix_statement")
        )

        val value = OperatorExpression(
            left,
            NumberLiteral("1"),
            token.value.substring(0, 1), OperatorType.INFIX
        )

        return AssignmentStatement(left, value)
    }
}
