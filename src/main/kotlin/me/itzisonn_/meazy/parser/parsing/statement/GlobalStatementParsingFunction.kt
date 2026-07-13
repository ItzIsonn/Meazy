package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.*
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseModifiers
import me.itzisonn_.meazy.util.text.translatable

object GlobalStatementParsingFunction : EmptyParsingFunction<Statement>() {
    override fun Parser.parse(): Statement {
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
                Pair(modifiers, false)
            )
        }

        if (isNext(variable)) {
            return parse(
                VariableDeclarationStatementParsingFunction,
                Pair(modifiers, false)
            )
        }

        throw InvalidStatementException(translatable("meazy:parser.exception.global_statement"))
    }
}
