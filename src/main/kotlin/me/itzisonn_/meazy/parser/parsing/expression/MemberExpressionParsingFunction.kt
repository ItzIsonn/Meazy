package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.memberAccess
import me.itzisonn_.meazy.lexer.TokenTypes.questionDot
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.CallExpression
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.MemberExpression
import me.itzisonn_.meazy.parser.ast.expression.Identifier
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object MemberExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        var receiver = parse(CallExpressionParsingFunction)

        while (isNext(memberAccess)) {
            val isNullSafe = consume(memberAccess, null).type == questionDot
            val member = parse(CallExpressionParsingFunction)

            if (member !is Identifier && member !is CallExpression) {
                throw UnexpectedTokenException(
                    translatable("parser.exception.member_expression")
                )
            }

            receiver = MemberExpression(receiver, member, isNullSafe)
        }

        return receiver
    }
}
