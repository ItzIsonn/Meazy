package me.itzisonn_.meazy.parser.parsing.expression

import me.itzisonn_.meazy.lexer.TokenTypeSets.addition
import me.itzisonn_.meazy.lexer.TokenTypeSets.multiplication
import me.itzisonn_.meazy.lexer.TokenTypes.power
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.expression.OperatorExpression
import me.itzisonn_.meazy.runtime.data.operator.OperatorType
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.runtime.data.operator.Operators

object AdditionExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        var left = parse(MultiplicationExpressionParsingFunction)

        while (isNext(addition)) {
            val operator = consume(addition, null).value
            val right = parse(MultiplicationExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}

object MultiplicationExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        var left = parse(PowerExpressionParsingFunction)

        while (isNext(multiplication)) {
            val operator = consume(multiplication, null).value
            val right = parse(PowerExpressionParsingFunction)
            left = OperatorExpression(left, right, operator, OperatorType.INFIX)
        }

        return left
    }
}

object PowerExpressionParsingFunction : EmptyParsingFunction<Expression>() {
    override fun Parser.parse(): Expression {
        var left = parse(InversionExpressionParsingFunction)

        while (isNext(power)) {
            consume(power, null)
            val right = parse(InversionExpressionParsingFunction)
            left = OperatorExpression(left, right, Operators.power)
        }

        return left
    }
}