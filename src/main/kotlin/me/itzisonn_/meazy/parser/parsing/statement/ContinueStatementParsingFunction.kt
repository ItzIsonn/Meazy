package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`continue`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.ContinueStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ContinueStatementParsingFunction : EmptyParsingFunction<ContinueStatement>() {
    override fun Parser.parse(): ContinueStatement {
        consume(`continue`, translatable("meazy:parser.expected.keyword", "continue"))
        return ContinueStatement()
    }
}
