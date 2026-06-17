package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`while`
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.WhileStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseBody
import me.itzisonn_.meazy.text.translatable

object WhileStatementParsingFunction : ParsingFunction<WhileStatement> {
    override fun Parser.parse(vararg extra: Any?): WhileStatement {
        consume(`while`, translatable("meazy:parser.expected.keyword", "while"))

        consume(
            leftParenthesis,
            translatable("meazy:parser.expected.start", "left_parenthesis", "while_condition")
        )
        val condition = parse(ExpressionParsingFunction)
        consume(
            rightParenthesis,
            translatable("meazy:parser.expected.end", "right_parenthesis", "while_condition")
        )

        consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "while_body"))
        val body = parseBody()
        consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "while_body"))

        return WhileStatement(condition, body)
    }
}
