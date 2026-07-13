package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.InterfaceDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
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

        consume(leftBrace, translatable("parser.expected.start", "left_brace", "interface_body"))

        if (isNext(rightBrace)) {
            consume(rightBrace, null)
            return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, listOf())
        }

        consume(newLine, translatable("parser.expected", "new_line"))

        val body = mutableListOf<Statement>()
        while (!isEndOfFile() && !isNext(rightBrace)) {
            val statement = parse(InterfaceBodyStatementParsingFunction)
            body.add(statement)
        }

        consume(rightBrace, translatable("parser.expected.end", "right_brace", "interface_body"))
        return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, body)
    }
}
