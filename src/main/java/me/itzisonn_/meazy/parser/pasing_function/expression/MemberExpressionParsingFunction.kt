package me.itzisonn_.meazy.parser.pasing_function.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.memberAccess
import me.itzisonn_.meazy.lexer.TokenTypes.questionDot
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.identifier.Identifier
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

object MemberExpressionParsingFunction : AbstractParsingFunction<Expression>("member_expression") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Expression {
        val parser = context.parser
        var receiver = parser.parse(CallExpressionParsingFunction)

        while (parser.current.type in memberAccess) {
            val isNullSafe = parser.consume().type == questionDot
            val member = parser.parse(CallExpressionParsingFunction)

            if (member !is Identifier && member !is CallExpression) {
                throw UnexpectedTokenException(
                    parser.current.line,
                    translatable("meazy:parser.exception.member_expression")
                )
            }

            receiver = MemberExpression(receiver, member, isNullSafe)
        }

        return receiver
    }
}
