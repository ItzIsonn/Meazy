package me.itzisonn_.meazy.runtime

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.lexer.TokenBehavior
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.UnknownTokenException
import me.itzisonn_.meazy.parser.ast.ParentMap
import me.itzisonn_.meazy.parser.ast.SymbolMap
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.parsing.statement.ProgramParsingFunction
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import java.lang.constant.ClassDesc

object RuntimeFunctions {
    fun tokenize(string: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var lineNumber = 1

        var i = 0
        while (i < string.length) {
            val substring = string.substring(i)
            var token: Token? = null

            for (tokenType in TokenTypes.getAll()) {
                if (tokenType.regex == null) continue

                val result = tokenType.regex.matchAt(substring, 0)
                if (result != null) {
                    val end = result.range.last + 1
                    val matched = result.value
                    if (!tokenType.canMatch(matched)) continue

                    if (token == null || token.value.length < matched.length) {
                        token = Token(lineNumber, i, end, tokenType, matched)
                    }
                }
            }

            if (token == null) {
                var errorString = substring.split("\n".toRegex()).dropLastWhile { it.isEmpty() }[0]
                if (errorString.length > 20) errorString = errorString.take(20) + "..."

                throw UnknownTokenException(lineNumber, errorString)
            }

            i += token.value.length - 1
            if (token.type.behavior != TokenBehavior.SKIP) tokens.add(token)

            lineNumber += token.value.length - token.value.replace("\n", "").length
            i++
        }

        tokens.add(Token(lineNumber, string.length, string.length, TokenTypes.endOfFile, ""))
        return tokens
    }



    fun parseTokens(programName: String, tokens: List<Token>): Program {
        val parser = Parser(tokens)
        return parser.parse(ProgramParsingFunction, programName)
    }



    fun compilePrograms(programs: List<Program>): Map<ClassDesc, ByteArray> {
        val globalEnvironment = GlobalEnvironment()
        val symbolMap = SymbolMap()

        val bytecodeBuilders = BytecodeBuilders.of(null, null)
        val instructionsSet = InstructionsSet(bytecodeBuilders)

        val parentMaps = programs.associateWith { program -> ParentMap(program) }
        fun Program.parentMap() = parentMaps[this] ?: error("There's no ParentMap for program $name")

        context(symbolMap) {
            for (program in programs) {
                context(program.parentMap()) { program.declare(globalEnvironment) }
            }

            for (program in programs) {
                context(program.parentMap()) { program.resolve(globalEnvironment) }
            }

            for (program in programs) {
                context(program.parentMap()) { program.emit(instructionsSet, globalEnvironment) }
            }
        }

        for (instruction in instructionsSet.instructions) {
            instruction.emit(bytecodeBuilders)
        }

        return bytecodeBuilders.classes
    }
}