package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes.id
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.import
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.ast.statement.ImportStatement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ImportStatementParsingFunction : ParsingFunction<ImportStatement> {
    override fun Parser.parse(vararg extra: Any?): ImportStatement {
        consume(import, translatable("meazy:parser.expected.keyword", "import"))

        val name = StringBuilder(
            consume(
                id, translatable("meazy:parser.expected.after_keyword", "id", "import")
            ).value
        )

        while (isNext(dot)) {
            consume(dot, null)
            name.append(".")
            name.append(consume(id, translatable("meazy:parser.expected", "id")).value)
        }

        return ImportStatement(name.toString())
    }
}
