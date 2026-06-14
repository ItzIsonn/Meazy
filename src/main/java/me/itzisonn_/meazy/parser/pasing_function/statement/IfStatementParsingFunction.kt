package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.`else`
import me.itzisonn_.meazy.lexer.TokenTypes.`if`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.IfStatement
import me.itzisonn_.meazy.parser.ast.statement.LocalStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class IfStatementParsingFunction : AbstractParsingFunction<IfStatement>("if_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): IfStatement {
        val parser = context.parser

        parser.next(`if`, translatable("meazy:parser.expected.keyword", "if"))
        parser.next(leftParenthesis, translatable("meazy:parser.expected.start", "left_parenthesis", "if_condition"))

        val condition = parser.parse<Expression>(getDefaultIdentifier("expression"))
        parser.next(rightParenthesis, translatable("meazy:parser.expected.end", "right_parenthesis", "if_condition"))

        var body = mutableListOf<LocalStatement>()
        if (parser.current.type == leftBrace) {
            parser.next()
            body = ParsingHelper.parseBody(context).toMutableList()
            parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
        }
        else body.add(parser.parse<LocalStatement>(getDefaultIdentifier("local_statement")))

        val elsePos = parser.pos + 1
        if (elsePos < parser.size && parser[elsePos].type == `else`) {
            parser.next(newLine, translatable("meazy:parser.expected.end_statement", "new_line"))
        }

        var elseStatement: IfStatement? = null
        if (parser.current.type == `else`) {
            parser.next()
            if (parser.current.type == `if`) {
                elseStatement = parser.parse<IfStatement>(getDefaultIdentifier("if_statement"))
            }
            else {
                var elseBody = mutableListOf<LocalStatement>()
                if (parser.current.type == leftBrace) {
                    parser.next()
                    elseBody = ParsingHelper.parseBody(context).toMutableList()
                    parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "if_body"))
                }
                else elseBody.add(
                    parser.parse<LocalStatement>(getDefaultIdentifier("local_statement"))
                )

                elseStatement = IfStatement(null, elseBody, null)
            }
        }

        return IfStatement(condition, body, elseStatement)
    }
}
