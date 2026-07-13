package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseModifiers
import me.itzisonn_.meazy.util.text.translatable

object InterfaceBodyStatementParsingFunction : ParsingFunction<Statement> {
    override fun Parser.parse(vararg extra: Any?): Statement {
        val modifiers = parseModifiers()

        if (isNext(function)) {
            return parse(
                FunctionDeclarationStatementParsingFunction,
                modifiers,
                true
            )
        }

        throw InvalidStatementException(
            translatable("meazy:parser.expected.statement", "interface_body")
        )
    }
}
