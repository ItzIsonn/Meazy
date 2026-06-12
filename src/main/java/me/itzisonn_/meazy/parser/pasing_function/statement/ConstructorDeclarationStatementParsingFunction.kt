package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class ConstructorDeclarationStatementParsingFunction : AbstractParsingFunction<ConstructorDeclarationStatement>("constructor_declaration_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): ConstructorDeclarationStatement {
        val parser = context.parser

        val modifiers = ParsingHelper.getModifiersFromExtra(extra)
        parser.next(constructor, translatable("meazy:parser.expected.keyword", "constructor"))

        val parameters = ParsingHelper.parseParameters(context)

        if (parser.current.type != leftBrace) {
            return ConstructorDeclarationStatement(modifiers, parameters, mutableListOf())
        }

        parser.next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "constructor_body"))
        val body = ParsingHelper.parseBody(context)
        parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "constructor_body"))

        return ConstructorDeclarationStatement(modifiers, parameters, body)
    }
}
