package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.Meazy.VERSION
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.parsing.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.util.text.translatable
import java.io.File

object ProgramParsingFunction : ParsingFunction<Program, File?> {
    override fun Parser.parse(data: File?): Program {
        val file = data

        val body = mutableListOf<Statement>()
        var headerStatement: Statement?

        while (true) {
            try {
                headerStatement = parse(HeaderStatementParsingFunction)
            }
            catch (_: UnexpectedTokenException) {
                break
            }

            consume(newLine, translatable("parser.expected", "new_line"))
            body.add(headerStatement)
        }

        while (!isEndOfFile()) {
            body.add(parse(GlobalStatementParsingFunction))

            if (!isEndOfFile()) {
                consume(newLine, translatable("parser.expected", "new_line"))
            }
        }

        return Program(file, VERSION, body)
    }
}
