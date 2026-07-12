package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypeSets.operatorAssign
import me.itzisonn_.meazy.lexer.TokenTypes.assign
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.parser.ast.statement.AssignmentStatement
import me.itzisonn_.meazy.parser.operator.OperatorType
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object AssignmentStatementParsingFunction : ParsingFunction<AssignmentStatement> {
    override fun Parser.parse(vararg extra: Any?): AssignmentStatement {
        val left = parse(ExpressionParsingFunction)

        if (isNext(assign)) {
            consume(assign, null)
            val value = parse(ExpressionParsingFunction)
            return AssignmentStatement(left, value)
        }
        else if (isNext(operatorAssign)) {
            val token = consume(operatorAssign, null)

            val value = OperatorExpression(
                left,
                parse(ExpressionParsingFunction),
                token.value.replace("=$".toRegex(), ""), OperatorType.INFIX
            )

            return AssignmentStatement(left, value)
        }

        throw UnexpectedTokenException(current, translatable("meazy:parser.expected.separator_statement", "assign", "assignment"))
    }
}
