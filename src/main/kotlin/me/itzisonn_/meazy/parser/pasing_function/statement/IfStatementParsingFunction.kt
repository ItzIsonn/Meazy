package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`else`
import me.itzisonn_.meazy.lexer.TokenTypes.`if`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.IfStatement
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseBody
import me.itzisonn_.meazy.util.text.translatable

object IfStatementParsingFunction : ParsingFunction<IfStatement> {
    override fun Parser.parse(vararg extra: Any?): IfStatement {
        consume(`if`, translatable("meazy:parser.expected.keyword", "if"))
        consume(leftParenthesis, translatable("meazy:parser.expected.start", "left_parenthesis", "if_condition"))

        val condition = parse(ExpressionParsingFunction)
        consume(rightParenthesis, translatable("meazy:parser.expected.end", "right_parenthesis", "if_condition"))

        var body: List<LocalStatement>
        if (isNext(leftBrace)) {
            consume(leftBrace, null)
            body = parseBody()
            consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
        }
        else body = listOf(parse(LocalStatementParsingFunction))

        val elsePos = pos + 1
        if (elsePos < size && this[elsePos].type == `else`) {
            consume(newLine, translatable("meazy:parser.expected.end_statement", "new_line"))
        }

        var elseStatement: IfStatement? = null
        if (isNext(`else`)) {
            consume(`else`, null)
            if (isNext(`if`)) {
                elseStatement = parse(IfStatementParsingFunction)
            }
            else {
                var elseBody: List<LocalStatement>
                if (isNext(leftBrace)) {
                    consume(leftBrace, null)
                    elseBody = parseBody()
                    consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
                }
                else elseBody = listOf(parse(LocalStatementParsingFunction))

                elseStatement = IfStatement(null, elseBody, null)
            }
        }

        return IfStatement(condition, body, elseStatement)
    }
}
