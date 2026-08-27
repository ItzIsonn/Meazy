package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.InterfaceDeclarationStatement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.util.text.translatable

object InterfaceDeclarationStatementParsingFunction : ParsingFunction<InterfaceDeclarationStatement, Set<Modifier>> {
    override fun Parser.parse(data: Set<Modifier>): InterfaceDeclarationStatement {
        val modifiers = data

        consume(`interface`, translatable("parser.expected.keyword", "interface"))
        val interfaceId = consume(id, translatable("parser.expected.after_keyword", "id", "interface")).value

        val baseClasses = mutableSetOf<String>()

        if (isNext(colon)) {
            consume(colon, null)
            baseClasses.add(consume(id, translatable("parser.expected", "id")).value)

            while (isNext(comma)) {
                consume(comma, null)
                baseClasses.add(consume(id, translatable("parser.expected", "id")).value)
            }
        }

        if (!isNext(leftBrace)) {
            return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, listOf())
        }

        val body = parseBody(InterfaceBodyStatementParsingFunction)
        return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, body)
    }
}
