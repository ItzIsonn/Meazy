package me.itzisonn_.meazy.parser.pasing_function.statement

import me.itzisonn_.meazy.MeazyMain.VERSION
import me.itzisonn_.meazy.lexer.TokenTypes.endOfFile
import me.itzisonn_.meazy.lexer.TokenTypes.newLine
import me.itzisonn_.meazy.parser.ParsingContext
import me.itzisonn_.meazy.parser.UnexpectedTokenException
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.ast.statement.Statement
import me.itzisonn_.meazy.parser.pasing_function.AbstractParsingFunction
import me.itzisonn_.meazy.text.translatable
import java.io.File

object ProgramParsingFunction : AbstractParsingFunction<Program>("program") {
    override fun parse(context: ParsingContext, vararg extra: Any?): Program {
        val file: File?
        require(extra.isNotEmpty()) { "Expected file as extra argument" }
        if (extra[0] is File) file = extra[0] as File
        else throw IllegalArgumentException("Expected file as extra argument")

        val parser = context.parser
        parser.skipNewLines()

        val body = mutableListOf<Statement>()
        var headerStatement: Statement?

        while (true) {
            try {
                headerStatement = parser.parse(HeaderStatementParsingFunction)
            }
            catch (_: UnexpectedTokenException) {
                break
            }

            parser.next(newLine, translatable("meazy:parser.expected", "new_line"))
            parser.skipNewLines()

            body.add(headerStatement)
        }

        parser.skipNewLines()

        while (parser.current.type != endOfFile) {
            body.add(parser.parse(GlobalStatementParsingFunction))

            if (parser.current.type != endOfFile) {
                parser.next(newLine, translatable("meazy:parser.expected", "new_line"))
                parser.skipNewLines()
            }
        }

        return Program(file, VERSION, body)
    }
}
