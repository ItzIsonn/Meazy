package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorPostfix
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.expression.literal.NumberLiteral
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object PostfixStatementParsingFunction : EmptyParsingFunction<AssignmentStatement>() {
    override fun Parser.parse(): AssignmentStatement {
        val left = parse(ExpressionParsingFunction)
        val token = consume(
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
