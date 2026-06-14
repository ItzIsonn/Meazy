package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorPostfix
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.text.translatable

object PostfixStatementParsingFunction : AbstractParsingFunction<AssignmentStatement>("postfix_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): AssignmentStatement {
        val parser = context.parser

        val left = parser.parse(ExpressionParsingFunction)
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
