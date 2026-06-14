package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`for`
import me.itzisonn_.meazy.lexer.TokenTypes.`in`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidSyntaxException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.expression.Expression
import me.itzisonn_.meazy.parser.ast.statement.ForeachStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class ForeachStatementParsingFunction : AbstractParsingFunction<ForeachStatement>("foreach_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): ForeachStatement {
        val parser = context.parser

        parser.next(`for`, translatable("meazy:parser.expected.keyword", "for"))
        parser.next(
            leftParenthesis,
            translatable("meazy:parser.expected.start", "left_parenthesis", "for_condition")
        )

        val isConstant = parser.consume(variable, translatable("meazy:parser.expected.keyword", "variable")).value == "val"
        val id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value

        val lineNumber = parser.current.line
        val dataType = ParsingHelper.parseDataType(context) ?: throw InvalidSyntaxException(
            lineNumber,
            translatable("meazy:parser.exception.foreach_variable_without_datatype")
        )

        parser.next(`in`, translatable("meazy:parser.expected.after_statement", "in", "variable_declaration"))
        val collection = parser.parse<Expression>(getDefaultIdentifier("expression"))

        parser.next(
            rightParenthesis,
            translatable("meazy:parser.expected.end", "right_parenthesis", "for_condition")
        )

        parser.next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "for_body"))
        val body = ParsingHelper.parseBody(context)
        parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "for_body"))

        return ForeachStatement(isConstant, id, dataType, collection, body)
    }
}
