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
import me.itzisonn_.meazy.text.translatable

object IfStatementParsingFunction : ParsingFunction<IfStatement>("if_statement") {
    override fun Parser.parse(vararg extra: Any?): IfStatement {
        next(`if`, translatable("meazy:parser.expected.keyword", "if"))
        next(leftParenthesis, translatable("meazy:parser.expected.start", "left_parenthesis", "if_condition"))

        val condition = parse(ExpressionParsingFunction)
        next(rightParenthesis, translatable("meazy:parser.expected.end", "right_parenthesis", "if_condition"))

        var body: List<LocalStatement>
        if (current.type == leftBrace) {
            next()
            body = parseBody()
            next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
        }
        else body = listOf(parse(LocalStatementParsingFunction))

        val elsePos = pos + 1
        if (elsePos < size && this[elsePos].type == `else`) {
            next(newLine, translatable("meazy:parser.expected.end_statement", "new_line"))
        }

        var elseStatement: IfStatement? = null
        if (current.type == `else`) {
            next()
            if (current.type == `if`) {
                elseStatement = parse(IfStatementParsingFunction)
            }
            else {
                var elseBody: List<LocalStatement>
                if (current.type == leftBrace) {
                    next()
                    elseBody = parseBody()
                    next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
                }
                else elseBody = listOf(parse(LocalStatementParsingFunction))

                elseStatement = IfStatement(null, elseBody, null)
            }
        }

        return IfStatement(condition, body, elseStatement)
    }
}
