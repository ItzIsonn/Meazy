package me.itzisonn_.meazy.runtime

import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.lexer.Token
import me.itzisonn_.meazy.lexer.TokenBehavior
import me.itzisonn_.meazy.lexer.TokenTypes
import me.itzisonn_.meazy.lexer.UnknownTokenException
import me.itzisonn_.meazy.parser.parsing.Parser
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.parsing.statement.ProgramParsingFunction
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import java.io.File
import java.lang.constant.ClassDesc
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility
import kotlin.reflect.typeOf

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
                if (errorString.length > 20) errorString = errorString.substring(0, 20) + "..."

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



    fun parseTokens(file: File?, tokens: List<Token>): Program {
        val parser = Parser(tokens)
        return parser.parse(ProgramParsingFunction, file)
    }



    fun compileProgram(program: Program): Map<ClassDesc, ByteArray> {
        val globalEnvironment = GlobalEnvironment()
        val bytecodeBuilders = BytecodeBuilders.of(null, null)
        val instructionsSet = InstructionsSet(bytecodeBuilders)

        program.declare(globalEnvironment)
        program.resolve(globalEnvironment)
        program.emit(instructionsSet, globalEnvironment)

        for (instruction in instructionsSet.instructions) {
            instruction.emit(bytecodeBuilders)
        }

        return bytecodeBuilders.classes
    }



    fun loadClassesAndRun(classes: Map<ClassDesc, ByteArray>) {
        val classLoader = ClassLoaderWrapper()
        val mainMethods = mutableSetOf<KCallable<*>>()

        for ((classDesc, classFile) in classes) {
            val loadedClass = classLoader.defineClass(classFile).kotlin

            val method = loadedClass.members.find { it.name == "main" }
            if (method == null) {
                System.err.println("No method main in class $classDesc")
                continue
            }

            if (method.returnType != typeOf<Unit>() || method.parameters.isNotEmpty()) {
                System.err.println("Main method has invalid signature in class $classDesc") //TODO
                continue
            }

            if (method.visibility != KVisibility.PUBLIC) {
                System.err.println("Main method is inaccessible in class $classDesc")
                continue
            }

            mainMethods += method
        }

        mainMethods.forEach { it.call() }
    }
}