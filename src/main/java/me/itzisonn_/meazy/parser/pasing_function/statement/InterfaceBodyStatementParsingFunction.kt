package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseModifiers
import me.itzisonn_.meazy.text.translatable

object InterfaceBodyStatementParsingFunction : ParsingFunction<Statement> {
    override fun Parser.parse(vararg extra: Any?): Statement {
        val modifiers = parseModifiers()

        if (current.type == function) {
            return parse(
                FunctionDeclarationStatementParsingFunction,
                modifiers,
                true
            )
        }

        throw InvalidStatementException(
            current.line,
            translatable("meazy:parser.expected.statement", "interface_body")
        )
    }
}
