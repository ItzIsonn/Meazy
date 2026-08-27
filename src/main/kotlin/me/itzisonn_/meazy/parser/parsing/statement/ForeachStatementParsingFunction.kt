package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.`for`
import me.itzisonn_.meazy.lexer.TokenTypes.`in`
import me.itzisonn_.meazy.lexer.TokenTypes.leftParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.rightParenthesis
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.ForeachStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.expression.ExpressionParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.parser.parsing.parseDataType
import me.itzisonn_.meazy.util.text.translatable

object ForeachStatementParsingFunction : EmptyParsingFunction<ForeachStatement>() {
    override fun Parser.parse(): ForeachStatement {
        consume(`for`, translatable("parser.expected.keyword", "for"))
        consume(
            leftParenthesis,
            translatable("parser.expected.start", "left_parenthesis", "for_condition")
        )

        val isConstant = consume(variable, translatable("parser.expected.keyword", "variable")).value == "val"
        val id = consume(TokenTypes.id, translatable("parser.expected", "id")).value

        val dataType = parseDataType() ?: throw InvalidSyntaxException(
            translatable("parser.exception.foreach_variable_without_datatype")
        )

        consume(`in`, translatable("parser.expected.after_statement", "in", "variable_declaration"))
        val collection = parse(ExpressionParsingFunction)

        consume(
            rightParenthesis,
            translatable("parser.expected.end", "right_parenthesis", "for_condition")
        )

        val body = parseBody(LocalStatementParsingFunction)
        return ForeachStatement(isConstant, id, dataType, collection, body)
    }
}
