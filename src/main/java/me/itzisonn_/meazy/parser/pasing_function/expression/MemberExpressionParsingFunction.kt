package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.memberAccess
import me.itzisonn_.meazy.lexer.TokenTypes.questionDot
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable

object MemberExpressionParsingFunction : ParsingFunction<Expression>("member_expression") {
    override fun Parser.parse(vararg extra: Any?): Expression {
        var receiver = parse(CallExpressionParsingFunction)

        while (current.type in memberAccess) {
            val isNullSafe = consume().type == questionDot
            val member = parse(CallExpressionParsingFunction)

            if (member !is Identifier && member !is CallExpression) {
                throw UnexpectedTokenException(
                    current.line,
                    translatable("meazy:parser.exception.member_expression")
                )
            }

            receiver = MemberExpression(receiver, member, isNullSafe)
        }

        return receiver
    }
}
