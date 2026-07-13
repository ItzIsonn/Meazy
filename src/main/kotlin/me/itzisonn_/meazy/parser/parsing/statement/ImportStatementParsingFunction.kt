package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.import
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.ImportStatement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ImportStatementParsingFunction : EmptyParsingFunction<ImportStatement>() {
    override fun Parser.parse(): ImportStatement {
        consume(import, translatable("parser.expected.keyword", "import"))

        val name = StringBuilder(
            consume(
                id, translatable("parser.expected.after_keyword", "id", "import")
            ).value
        )

        while (isNext(dot)) {
            consume(dot, null)
            name.append(".")
            name.append(consume(id, translatable("parser.expected", "id")).value)
        }

        return ImportStatement(name.toString())
    }
}
