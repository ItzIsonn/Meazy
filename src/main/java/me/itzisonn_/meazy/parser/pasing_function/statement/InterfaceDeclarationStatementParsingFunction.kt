package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.`interface`
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.rightBrace
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.InterfaceDeclarationStatement
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.parser.pasing_function.ParsingHelper
import me.itzisonn_.meazy.text.translatable

class InterfaceDeclarationStatementParsingFunction :
    AbstractParsingFunction<InterfaceDeclarationStatement>("interface_declaration_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): InterfaceDeclarationStatement {
        val parser = context.parser
        val modifiers = ParsingHelper.getModifiersFromExtra(extra)

        parser.consume(`interface`, translatable("meazy:parser.expected.keyword", "interface"))
        val id = parser.consume(TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "interface")).value

        val baseClasses = mutableSetOf<String>()

        if (parser.current.type == colon) {
            do {
                parser.next()
                baseClasses.add(parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value)
            }
            while (parser.current.type == comma)
        }

        if (parser.current.type != leftBrace) {
            return InterfaceDeclarationStatement(modifiers, id, baseClasses, listOf())
        }

        parser.next(leftBrace, translatable("meazy:parser.expected.start", "left_brace", "interface_body"))

        if (parser.current.type == rightBrace) {
            parser.next()
            return InterfaceDeclarationStatement(modifiers, id, baseClasses, listOf())
        }

        parser.consume(newLine, translatable("meazy:parser.expected", "new_line"))
        parser.skipNewLines()

        val body = mutableListOf<Statement>()
        while (parser.current.type != endOfFile && parser.current.type != rightBrace) {
            val statement = parser.parse<Statement>(getDefaultIdentifier("interface_body_statement"))
            body.add(statement)
            parser.skipNewLines()
        }

        parser.next(rightBrace, translatable("meazy:parser.expected.end", "right_brace", "interface_body"))
        return InterfaceDeclarationStatement(modifiers, id, baseClasses, body)
    }
}
