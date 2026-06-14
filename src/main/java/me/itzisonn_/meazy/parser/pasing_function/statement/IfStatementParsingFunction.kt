package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`else`
import me.itzisonn_.meazy.lexer.TokenTypes.`if`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.IfStatement
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.text.translatable

object IfStatementParsingFunction : AbstractParsingFunction<IfStatement>("if_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): IfStatement {
        val parser = context.parser

        parser.next(`if`, translatable("meazy:parser.expected.keyword", "if"))
        parser.next(leftParenthesis, translatable("meazy:parser.expected.start", "left_parenthesis", "if_condition"))

        val condition = parser.parse(ExpressionParsingFunction)
        parser.next(rightParenthesis, translatable("meazy:parser.expected.end", "right_parenthesis", "if_condition"))

        var body: List<LocalStatement>
        if (parser.current.type == leftBrace) {
            parser.next()
            body = ParsingHelper.parseBody(context)
            parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
        }
        else body = listOf(parser.parse(LocalStatementParsingFunction))

        val elsePos = parser.pos + 1
        if (elsePos < parser.size && parser[elsePos].type == `else`) {
            parser.next(newLine, translatable("meazy:parser.expected.end_statement", "new_line"))
        }

        var elseStatement: IfStatement? = null
        if (parser.current.type == `else`) {
            parser.next()
            if (parser.current.type == `if`) {
                elseStatement = parser.parse(IfStatementParsingFunction)
            }
            else {
                var elseBody: List<LocalStatement>
                if (parser.current.type == leftBrace) {
                    parser.next()
                    elseBody = ParsingHelper.parseBody(context)
                    parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
                }
                else elseBody = listOf(parser.parse(LocalStatementParsingFunction))

                elseStatement = IfStatement(null, elseBody, null)
            }
        }

        return IfStatement(condition, body, elseStatement)
    }
}
