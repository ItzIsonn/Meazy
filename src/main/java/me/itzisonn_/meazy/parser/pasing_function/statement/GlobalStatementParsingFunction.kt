package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.InvalidStatementException
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseModifiers
import me.itzisonn_.meazy.text.translatable

object GlobalStatementParsingFunction : ParsingFunction<Statement>("global_statement") {
    override fun Parser.parse(vararg extra: Any?): Statement {
        val modifiers = parseModifiers()

        if (current.type == `class`) {
            return parse(
                ClassDeclarationStatementParsingFunction,
                modifiers
            )
        }
        if (current.type == `interface`) {
            return parse(
                InterfaceDeclarationStatementParsingFunction,
                modifiers
            )
        }
        if (current.type == function) {
            return parse(
                FunctionDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }
        if (current.type == variable) {
            return parse(
                VariableDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }

        throw InvalidStatementException(current.line, translatable("meazy:parser.exception.global_statement"))
    }
}
