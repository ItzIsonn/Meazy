package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

object InterfaceBodyStatementParsingFunction : AbstractParsingFunction<Statement>("interface_body_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Statement {
        val parser = context.parser
        val modifiers = ParsingHelper.parseModifiers(context)

        if (parser.current.type == function) {
            return parser.parse(
                FunctionDeclarationStatementParsingFunction,
                modifiers,
                true
            )
        }

        throw InvalidStatementException(
            parser.current.line,
            translatable("meazy:parser.expected.statement", "interface_body")
        )
    }
}
