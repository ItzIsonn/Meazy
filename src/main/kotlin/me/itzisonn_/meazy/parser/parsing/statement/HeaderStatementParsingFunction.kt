package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`import`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object HeaderStatementParsingFunction : ParsingFunction<Statement> {
    override fun Parser.parse(vararg extra: Any?): Statement {
        if (isNext(`import`)) {
            return parse(ImportStatementParsingFunction)
        }

        throw UnexpectedTokenException(translatable("meazy:parser.expected.statement", "header"))
    }
}
