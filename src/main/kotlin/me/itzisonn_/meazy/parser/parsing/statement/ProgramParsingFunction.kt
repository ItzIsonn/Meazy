package me.itzisonn_.meazy.parser.parsing.statement

import me.itzisonn_.meazy.Meazy.VERSION
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.lexer.TokenTypes.`package`
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.parsing.ParsingException
import me.itzisonn_.meazy.parser.parsing.ParsingFunction
import me.itzisonn_.meazy.util.text.translatable

object ProgramParsingFunction : ParsingFunction<Program, String> {
    override fun Parser.parse(data: String): Program {
        val name = data

        val body = mutableListOf<Statement>()
        if (isNext(`package`)) body.add(parse(PackageStatementParsingFunction))

        while (true) {
            val importStatement =
                try {
                    parse(ImportStatementParsingFunction)
                }
                catch (_: ParsingException) {
                    break
                }

            consume(newLine, translatable("parser.expected", "new_line"))
            body.add(importStatement)
        }

        while (!isEndOfFile()) {
            body.add(parse(GlobalStatementParsingFunction))

            if (!isEndOfFile()) {
                consume(newLine, translatable("parser.expected", "new_line"))
            }
        }

        return Program(name, VERSION, body)
    }
}
