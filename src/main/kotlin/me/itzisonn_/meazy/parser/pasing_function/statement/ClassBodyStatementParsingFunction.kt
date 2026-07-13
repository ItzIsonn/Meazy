package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.function
import me.itzisonn_.meazy.lexer.TokenTypes.variable
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.parseModifiers
import me.itzisonn_.meazy.util.text.translatable

object ClassBodyStatementParsingFunction : ParsingFunction<Statement> {
    override fun Parser.parse(vararg extra: Any?): Statement {
        val modifiers = parseModifiers()

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
                true
            )
        }

        if (isNext(constructor)) {
            return parse(
                ConstructorDeclarationStatementParsingFunction,
                modifiers
            )
        }

        throw InvalidStatementException(translatable("meazy:parser.expected.statement", "class_body"))
    }
}
