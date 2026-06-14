package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

object GlobalStatementParsingFunction : AbstractParsingFunction<Statement>("global_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Statement {
        val parser = context.parser
        val modifiers = ParsingHelper.parseModifiers(context)

        if (parser.current.type == `class`) {
            return parser.parse(
                ClassDeclarationStatementParsingFunction,
                modifiers
            )
        }
        if (parser.current.type == `interface`) {
            return parser.parse(
                InterfaceDeclarationStatementParsingFunction,
                modifiers
            )
        }
        if (parser.current.type == function) {
            return parser.parse(
                FunctionDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }
        if (parser.current.type == variable) {
            return parser.parse(
                VariableDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }

        throw InvalidStatementException(parser.current.line, translatable("meazy:parser.exception.global_statement"))
    }
}
