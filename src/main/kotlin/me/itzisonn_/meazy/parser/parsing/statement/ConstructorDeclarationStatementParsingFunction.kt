package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.getModifiersFromExtra
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.parser.parsing.parseParameters
import me.itzisonn_.meazy.util.text.translatable

object ConstructorDeclarationStatementParsingFunction : ParsingFunction<ConstructorDeclarationStatement> {
    override fun Parser.parse(vararg extra: Any?): ConstructorDeclarationStatement {
        val modifiers = getModifiersFromExtra(extra)
        consume(constructor, translatable("meazy:parser.expected.keyword", "constructor"))

        val parameters = parseParameters()
        if (!isNext(leftBrace)) {
            return ConstructorDeclarationStatement(modifiers, parameters, mutableListOf())
        }

        consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "constructor_body"))
        val body = parseBody()
        consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "constructor_body"))

        return ConstructorDeclarationStatement(modifiers, parameters, body)
    }
}
