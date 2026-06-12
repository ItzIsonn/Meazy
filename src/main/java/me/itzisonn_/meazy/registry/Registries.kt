package me.itzisonn_.meazy.registry

import me.itzisonn_.meazy.MeazyMain
import me.itzisonn_.meazy.MeazyMain.getDefaultIdentifier
import me.itzisonn_.meazy.command.Commands
import me.itzisonn_.meazy.command.AbstractCommand
import me.itzisonn_.meazy.instruction.BytecodeBuilders
import me.itzisonn_.meazy.instruction.InstructionsSet
import me.itzisonn_.meazy.lexer.*
import me.itzisonn_.meazy.parser.*
import me.itzisonn_.meazy.parser.ast.statement.Program
import me.itzisonn_.meazy.parser.modifier.Modifier
import me.itzisonn_.meazy.parser.modifier.Modifiers
import me.itzisonn_.meazy.parser.operator.Operators
import me.itzisonn_.meazy.parser.pasing_function.ParsingFunctions
import me.itzisonn_.meazy.registry.Registries.PARSING_FUNCTIONS
import me.itzisonn_.meazy.registry.Registries.TOKEN_TYPES
import me.itzisonn_.meazy.runtime.ClassLoaderWrapper
import me.itzisonn_.meazy.runtime.environment.GlobalEnvironment
import me.itzisonn_.meazy.text.Language
import me.itzisonn_.registry.multiple_entry.OrderedRegistry
import me.itzisonn_.registry.multiple_entry.SetRegistry
import java.io.File
import java.lang.constant.ClassDesc
import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility
import kotlin.reflect.typeOf

/**
 * All basic Registries
 */
object Registries {
    private var isInitialized = false

    /**
     * Registry for all Languages
     * 
     * @see Language
     */
    val LANGUAGES = LanguageRegistry()



    /**
     * Registry for all Commands
     * 
     * @see AbstractCommand
     */
    val COMMANDS = CommandRegistry()



    /**
     * Registry for all TokenTypes
     */
    val TOKEN_TYPES = SetRegistry<TokenType>()

    /**
     * Registry for all TokenTypesSets
     */
    val TOKEN_TYPE_SETS = SetRegistry<TokenTypeSet>()

    /**
     * Registry for tokenization function that is used to tokenize given string
     * 
     * @see Token
     * @see TOKEN_TYPES
     */
    lateinit var tokenizationFunction: (String) -> List<Token>
        private set



    /**
     * Registry for all Modifiers
     */
    @JvmField
    val MODIFIERS = SetRegistry<Modifier>()

    /**
     * Registry for all Operators
     */
    @JvmField
    val OPERATORS = OperatorRegistry()

    /**
     * Registry for all ParsingFunctions
     * 
     * @see ParsingFunction
     * @see Parser
     */
    @JvmField
    val PARSING_FUNCTIONS = OrderedRegistry<ParsingFunction<*>>()

    /**
     * Registry for function that uses [PARSING_FUNCTIONS] to parse tokens into [Program]
     * 
     * @see ParsingFunction
     * @see Parser
     */
    lateinit var parseTokensFunction: (File?, List<Token>) -> Program
        private set



    /**
     * Registry for function that compiles [Program] to bytecode
     */
    lateinit var compileProgramFunction: (Program) -> Map<ClassDesc, ByteArray>
        private set

    /**
     * Registry for function that runs [Program]
     */
    lateinit var runProgramFunction: (Map<ClassDesc, ByteArray>) -> Unit
        private set



    /**
     * Initializes Registries
     * 
     * 
     * *Don't use this method because it's called once at [MeazyMain] initialization*
     * 
     * @throws IllegalStateException If Registries has already been initialized
     */
    fun initialize() {
        check(!isInitialized) { "Registries have already been initialized" }
        isInitialized = true

        LANGUAGES.register(getDefaultIdentifier("english"), Language("en", "English"))
        LANGUAGES.register(getDefaultIdentifier("russian"), Language("ru", "Русский"))

        Commands.register()
        TokenTypes.register()
        Modifiers.register()
        Operators.register()
        ParsingFunctions.REGISTER()

        tokenizationFunction = { lines ->
            val tokens = mutableListOf<Token>()
            var lineNumber = 1

            var i = 0
            while (i < lines.length) {
                val string = lines.substring(i)
                var token: Token? = null

                for (entry in TOKEN_TYPES.getEntries()) {
                    val tokenType = entry.getValue()!!
                    if (tokenType.pattern == null) continue

                    val matcher = tokenType.pattern.matcher(string)
                    if (matcher.find()) {
                        val end = matcher.end()
                        val matched = string.substring(0, end)
                        if (!tokenType.canMatch.invoke(matched)) continue

                        if (token == null || token.value.length < matched.length) {
                            token = Token(lineNumber, i, end, tokenType, matched)
                        }
                    }
                }

                if (token == null) {
                    var errorString = string.split("\n".toRegex()).dropLastWhile { it.isEmpty() }[0]
                    if (errorString.length > 20) errorString = errorString.substring(0, 20) + "..."

                    throw UnknownTokenException(lineNumber, errorString)
                }

                i += token.value.length - 1
                if (!token.type.shouldSkip) tokens.add(token)

                lineNumber += token.value.length - token.value.replace("\n", "").length
                i++
            }

            tokens.add(Token(lineNumber, lines.length, lines.length, TokenTypes.endOfFile, ""))
            tokens
        }

        parseTokensFunction = { file, tokens ->
            val parsingContext = ParsingContext(tokens)
            val parser = parsingContext.parser

            parser.parse(getDefaultIdentifier("program"), Program::class.java, file)
        }

        compileProgramFunction = { program ->
            val globalEnvironment = GlobalEnvironment()
            val bytecodeBuilders = BytecodeBuilders.of(null, null)
            val instructionsSet = InstructionsSet(bytecodeBuilders)

            program.declare(globalEnvironment)
            program.resolve(globalEnvironment)
            program.emit(instructionsSet, globalEnvironment)

            for (instruction in instructionsSet.instructions) {
                instruction.emit(bytecodeBuilders)
            }
            bytecodeBuilders.getClasses()
        }

        runProgramFunction = { classes ->
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
}