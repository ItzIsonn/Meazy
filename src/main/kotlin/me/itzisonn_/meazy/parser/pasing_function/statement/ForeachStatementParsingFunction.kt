package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`for`
import me.itzisonn_.meazy.lexer.TokenTypes.`in`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.ForeachStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseBody
import me.itzisonn_.meazy.parser.pasing_function.parseDataType
import me.itzisonn_.meazy.util.text.translatable

object ForeachStatementParsingFunction : ParsingFunction<ForeachStatement> {
    override fun Parser.parse(vararg extra: Any?): ForeachStatement {
        consume(`for`, translatable("meazy:parser.expected.keyword", "for"))
        consume(
            leftParenthesis,
            translatable("meazy:parser.expected.start", "left_parenthesis", "for_condition")
        )

        val isConstant = consume(variable, translatable("meazy:parser.expected.keyword", "variable")).value == "val"
        val id = consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value

        val dataType = parseDataType() ?: throw InvalidSyntaxException(
            translatable("meazy:parser.exception.foreach_variable_without_datatype")
        )

        consume(`in`, translatable("meazy:parser.expected.after_statement", "in", "variable_declaration"))
        val collection = parse(ExpressionParsingFunction)

        consume(
            rightParenthesis,
            translatable("meazy:parser.expected.end", "right_parenthesis", "for_condition")
        )

        consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "for_body"))
        val body = parseBody()
        consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "for_body"))

        return ForeachStatement(isConstant, id, dataType, collection, body)
    }
}
