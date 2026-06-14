package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.lexer.TokenTypes.`import`
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.statement.ImportStatement
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable

class HeaderStatementParsingFunction : AbstractParsingFunction<Statement>("header_statement") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Statement {
        val parser = context.parser

        if (parser.current.type == `import`) {
            return parser.parse<ImportStatement>(getDefaultIdentifier("import_statement"))
        }

        throw UnexpectedTokenException(parser.current.line, translatable("meazy:parser.expected.statement", "header"))
    }
}
