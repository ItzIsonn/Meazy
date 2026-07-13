package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseModifiers
import me.itzisonn_.meazy.util.text.translatable

object ClassBodyStatementParsingFunction : EmptyParsingFunction<Statement>() {
    override fun Parser.parse(): Statement {
        val modifiers = parseModifiers()

        if (isNext(function)) {
            return parse(
                FunctionDeclarationStatementParsingFunction,
                Pair(modifiers, false)
            )
        }

        if (isNext(variable)) {
            return parse(
                VariableDeclarationStatementParsingFunction,
                Pair(modifiers, true)
            )
        }

        if (isNext(constructor)) {
            return parse(
                ConstructorDeclarationStatementParsingFunction,
                modifiers
            )
        }

        throw InvalidStatementException(translatable("parser.expected.statement", "class_body"))
    }
}
