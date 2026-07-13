package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseModifiers
import me.itzisonn_.meazy.util.text.translatable

object GlobalStatementParsingFunction : ParsingFunction<Statement> {
    override fun Parser.parse(vararg extra: Any?): Statement {
        val modifiers = parseModifiers()

        if (isNext(`class`)) {
            return parse(
                ClassDeclarationStatementParsingFunction,
                modifiers
            )
        }

        if (isNext(`interface`)) {
            return parse(
                InterfaceDeclarationStatementParsingFunction,
                modifiers
            )
        }

        if (isNext(function)) {
            return parse(
                FunctionDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }

        if (isNext(variable)) {
            return parse(
                VariableDeclarationStatementParsingFunction,
                modifiers,
                false
            )
        }

        throw InvalidStatementException(translatable("meazy:parser.exception.global_statement"))
    }
}
