package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.lexer.TokenTypes.`import`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.parsing.EmptyParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object HeaderStatementParsingFunction : EmptyParsingFunction<Statement>() {
    override fun Parser.parse(): Statement {
        if (isNext(`import`)) {
            return parse(ImportStatementParsingFunction)
        }

        throw UnexpectedTokenException(translatable("parser.expected.statement", "header"))
    }
}
