package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`class`
import me.itzisonn_.meazy.lexer.TokenTypes.colon
import me.itzisonn_.meazy.lexer.TokenTypes.comma
import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.leftBrace
import me.itzisonn_.meazy.parser.ast.statement.ClassDeclarationStatement
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.parser.parsing.parseBody
import me.itzisonn_.meazy.runtime.data.modifier.Modifier
import me.itzisonn_.meazy.util.text.translatable

object ClassDeclarationStatementParsingFunction : ParsingFunction<ClassDeclarationStatement, Set<Modifier>> {
    override fun Parser.parse(data: Set<Modifier>): ClassDeclarationStatement {
        val modifiers = data.toMutableSet()

        consume(`class`, translatable("parser.expected.keyword", "class"))
        val classId = consume(id, translatable("parser.expected.after_keyword", "id", "class")).value

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
            return ClassDeclarationStatement(modifiers, classId, baseClasses, listOf())
        }

        val body = parseBody(ClassBodyStatementParsingFunction)
        return ClassDeclarationStatement(modifiers, classId, baseClasses, body)
    }
}