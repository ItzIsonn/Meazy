package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.TokenTypes.dot
import me.itzisonn_.meazy.lexer.TokenTypes.import
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.ast.statement.ImportStatement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class ImportStatementParsingFunction : AbstractParsingFunction<ImportStatement>("import_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): ImportStatement {
        val parser = context.parser
        parser.next(import, translatable("meazy:parser.expected.keyword", "import"))

        val name = StringBuilder(
            parser.consume(
                TokenTypes.id, translatable("meazy:parser.expected.after_keyword", "id", "import")
            ).value
        )

        while (parser.current.type == dot) {
            parser.next()
            name.append(".")
            name.append(parser.consume(TokenTypes.id, translatable("meazy:parser.expected", "id")).value)
        }

        return ImportStatement(name.toString())
    }
}
