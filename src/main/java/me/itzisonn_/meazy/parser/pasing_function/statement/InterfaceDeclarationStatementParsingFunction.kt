package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
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
import me.itzisonn_.meazy.text.translatable

object InterfaceDeclarationStatementParsingFunction : ParsingFunction<InterfaceDeclarationStatement>("interface_declaration_statement") {
    override fun Parser.parse(vararg extra: Any?): InterfaceDeclarationStatement {
        val modifiers = getModifiersFromExtra(extra)

        consume(`interface`, translatable("meazy:parser.expected.keyword", "interface"))
        val id = consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "interface")).value

        val baseClasses = mutableSetOf<String>()
        if (current.type == colon) {
            do {
                next()
                baseClasses.add(consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value)
            }
            while (current.type == comma)
        }

        if (current.type != leftBrace) {
            return InterfaceDeclarationStatement(modifiers, id, baseClasses, listOf())
        }

        next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "interface_body"))

        if (current.type == rightBrace) {
            next()
            return InterfaceDeclarationStatement(modifiers, id, baseClasses, listOf())
        }

        consume(newLine, translatable("meazy:parser.expected", "new_line"))
        skipNewLines()

        val body = mutableListOf<Statement>()
        while (current.type != endOfFile && current.type != rightBrace) {
            val statement = parse(InterfaceBodyStatementParsingFunction)
            body.add(statement)
            skipNewLines()
        }

        next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "interface_body"))
        return InterfaceDeclarationStatement(modifiers, id, baseClasses, body)
    }
}
