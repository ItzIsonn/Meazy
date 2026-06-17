package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.getModifiersFromExtra
import me.itzisonn_.meazy.parser.pasing_function.parseBody
import me.itzisonn_.meazy.parser.pasing_function.parseParameters
import me.itzisonn_.meazy.text.translatable

object ConstructorDeclarationStatementParsingFunction : ParsingFunction<ConstructorDeclarationStatement> {
    override fun Parser.parse(vararg extra: Any?): ConstructorDeclarationStatement {
        val modifiers = getModifiersFromExtra(extra)
        next(constructor, translatable("meazy:parser.expected.keyword", "constructor"))

        val parameters = parseParameters()
        if (current.type != leftBrace) {
            return ConstructorDeclarationStatement(modifiers, parameters, mutableListOf())
        }

        next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "constructor_body"))
        val body = parseBody()
        next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "constructor_body"))

        return ConstructorDeclarationStatement(modifiers, parameters, body)
    }
}
