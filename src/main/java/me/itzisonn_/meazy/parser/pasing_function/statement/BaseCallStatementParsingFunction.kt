package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.base
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.BaseCallStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

object BaseCallStatementParsingFunction : AbstractParsingFunction<BaseCallStatement>("base_call_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): BaseCallStatement {
        context.parser.next(base, translatable("meazy:parser.expected.start_statement", "base", "base_call"))
        return BaseCallStatement(ParsingHelper.parseArgs(context))
    }
}
