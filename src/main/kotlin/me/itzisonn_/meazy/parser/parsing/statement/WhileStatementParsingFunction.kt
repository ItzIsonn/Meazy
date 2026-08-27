package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.`while`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.WhileStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.util.text.translatable

object WhileStatementParsingFunction : EmptyParsingFunction<WhileStatement>() {
    override fun Parser.parse(): WhileStatement {
        consume(`while`, translatable("parser.expected.keyword", "while"))

        consume(
            leftParenthesis,
            translatable("parser.expected.start", "left_parenthesis", "while_condition")
        )
        val condition = parse(ExpressionParsingFunction)
        consume(
            rightParenthesis,
            translatable("parser.expected.end", "right_parenthesis", "while_condition")
        )

        val body = parseBody(LocalStatementParsingFunction)
        return WhileStatement(condition, body)
    }
}
