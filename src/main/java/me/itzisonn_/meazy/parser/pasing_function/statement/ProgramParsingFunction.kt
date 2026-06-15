package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.VERSION
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.parser.Parser
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunction
import me.itzisonn_.meazy.text.translatable
import java.io.File

object ProgramParsingFunction : ParsingFunction<Program>("program") {
    override fun Parser.parse(vararg extra: Any?): Program {
        val file: File?
        require(extra.isNotEmpty()) { "Expected file as extra argument" }
        if (extra[0] is File) file = extra[0] as File
        else throw IllegalArgumentException("Expected file as extra argument")

        skipNewLines()

        val body = mutableListOf<Statement>()
        var headerStatement: Statement?

        while (true) {
            try {
                headerStatement = parse(HeaderStatementParsingFunction)
            }
            catch (_: UnexpectedTokenException) {
                break
            }

            next(newLine, translatable("meazy:parser.expected", "new_line"))
            skipNewLines()

            body.add(headerStatement)
        }

        skipNewLines()

        while (current.type != endOfFile) {
            body.add(parse(GlobalStatementParsingFunction))

            if (current.type != endOfFile) {
                next(newLine, translatable("meazy:parser.expected", "new_line"))
                skipNewLines()
            }
        }

        return Program(file, VERSION, body)
    }
}
