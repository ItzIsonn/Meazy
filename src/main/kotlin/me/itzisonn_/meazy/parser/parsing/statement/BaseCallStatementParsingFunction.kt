package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.base
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseArgs
import me.itzisonn_.meazy.util.text.translatable

object BaseCallStatementParsingFunction : EmptyParsingFunction<BaseCallStatement>() {
    override fun Parser.parse(): BaseCallStatement {
        consume(base, translatable("parser.expected.start_statement", "base", "base_call"))
        return BaseCallStatement(parseArgs())
    }
}
