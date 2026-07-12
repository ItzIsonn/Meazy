package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.VERSION
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.util.text.translatable
import java.io.File

object ProgramParsingFunction : ParsingFunction<Program> {
    override fun Parser.parse(vararg extra: Any?): Program {
        require(extra.isNotEmpty()) { "Expected file as extra argument" }
        val file = if (extra[0] is File) extra[0] as File else null

        val body = mutableListOf<Statement>()
        var headerStatement: Statement?

        while (true) {
            try {
                headerStatement = parse(HeaderStatementParsingFunction)
            }
            catch (_: UnexpectedTokenException) {
                break
            }

            consume(newLine, translatable("meazy:parser.expected", "new_line"))
            body.add(headerStatement)
        }

        while (current.type != endOfFile) {
            body.add(parse(GlobalStatementParsingFunction))

            if (current.type != endOfFile) {
                consume(newLine, translatable("meazy:parser.expected", "new_line"))
            }
        }

        return Program(file, VERSION, body)
    }
}
