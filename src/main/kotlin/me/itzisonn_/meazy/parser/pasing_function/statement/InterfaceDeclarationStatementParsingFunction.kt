package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.InterfaceDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.getModifiersFromExtra
import me.itzisonn_.meazy.util.text.translatable

object InterfaceDeclarationStatementParsingFunction : ParsingFunction<InterfaceDeclarationStatement> {
    override fun Parser.parse(vararg extra: Any?): InterfaceDeclarationStatement {
        val modifiers = getModifiersFromExtra(extra)

        consume(`interface`, translatable("meazy:parser.expected.keyword", "interface"))
        val interfaceId = consume(id, translatable("meazy:parser.expected.after_keyword", "id", "interface")).value

        val baseClasses = mutableSetOf<String>()

        if (isNext(colon)) {
            consume(colon, null)
            baseClasses.add(consume(id, translatable("meazy:parser.expected", "id")).value)

            while (isNext(comma)) {
                consume(comma, null)
                baseClasses.add(consume(id, translatable("meazy:parser.expected", "id")).value)
            }
        }

        if (!isNext(leftBrace)) {
            return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, listOf())
        }

        consume(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "interface_body"))

        if (isNext(rightBrace)) {
            consume(rightBrace, null)
            return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, listOf())
        }

        consume(newLine, translatable("meazy:parser.expected", "new_line"))

        val body = mutableListOf<Statement>()
        while (!isEndOfFile() && !isNext(rightBrace)) {
            val statement = parse(InterfaceBodyStatementParsingFunction)
            body.add(statement)
        }

        consume(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "interface_body"))
        return InterfaceDeclarationStatement(modifiers, interfaceId, baseClasses, body)
    }
}
