package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.base
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseArgs
import me.itzisonn_.meazy.text.translatable

object BaseCallStatementParsingFunction : ParsingFunction<BaseCallStatement>("base_call_statement") {
    override fun Parser.parse(vararg extra: Any?): BaseCallStatement {
        next(base, translatable("meazy:parser.expected.start_statement", "base", "base_call"))
        return BaseCallStatement(parseArgs())
    }
}
