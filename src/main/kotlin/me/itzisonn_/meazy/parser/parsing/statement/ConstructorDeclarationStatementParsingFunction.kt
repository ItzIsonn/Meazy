package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.constructor
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.ConstructorDeclarationStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.parser.parsing.parseParameters
import me.itzisonn_.meazy.util.text.translatable

object ConstructorDeclarationStatementParsingFunction : ParsingFunction<ConstructorDeclarationStatement, Set<Modifier>> {
    override fun Parser.parse(data: Set<Modifier>): ConstructorDeclarationStatement {
        val modifiers = data
        consume(constructor, translatable("parser.expected.keyword", "constructor"))

        val parameters = parseParameters()
        if (!isNext(leftBrace)) {
            return ConstructorDeclarationStatement(modifiers, parameters, listOf())
        }

        val body = parseBody(LocalStatementParsingFunction)
        return ConstructorDeclarationStatement(modifiers, parameters, body)
    }
}
