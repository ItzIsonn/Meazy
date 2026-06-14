package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`while`
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.WhileStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.text.translatable

object WhileStatementParsingFunction : AbstractParsingFunction<WhileStatement>("while_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): WhileStatement {
        val parser = context.parser

        parser.consume(`while`, translatable("meazy:parser.expected.keyword", "while"))

        parser.consume(
            leftParenthesis,
            translatable("meazy:parser.expected.start", "left_parenthesis", "while_condition")
        )
        val condition = parser.parse(ExpressionParsingFunction)
        parser.consume(
            rightParenthesis,
            translatable("meazy:parser.expected.end", "right_parenthesis", "while_condition")
        )

        parser.consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "while_body"))
        val body = ParsingHelper.parseBody(context)
        parser.consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "while_body"))

        return WhileStatement(condition, body)
    }
}
