package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.`package`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.PackageStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object PackageStatementParsingFunction : EmptyParsingFunction<PackageStatement>() {
    override fun Parser.parse(): PackageStatement {
        consume(`package`, translatable("parser.expected.keyword", "package"))

        val name = StringBuilder(consume(id, translatable("parser.expected.after_keyword", "id", "package")).value)

        while (isNext(dot)) {
            consume(dot, null)
            name.append(".")
            name.append(consume(id, translatable("parser.expected", "id")).value)
        }

        return PackageStatement(name.toString())
    }
}
